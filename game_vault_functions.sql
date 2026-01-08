USE game_vault_db;

DROP EVENT IF EXISTS UpdateRentalFeeForLateRentals;
DROP TRIGGER IF EXISTS ApplyDiscountBeforeInsert;
DROP TRIGGER IF EXISTS PreventRentalWithoutMembership;
DROP PROCEDURE IF EXISTS AddCustomer;
DROP PROCEDURE IF EXISTS InsertOrderWithDetails;
DROP PROCEDURE IF EXISTS SearchGames;

DELIMITER $$

CREATE EVENT UpdateRentalFeeForLateRentals
ON SCHEDULE EVERY 1 DAY -- Set to go off daily
STARTS CURRENT_TIMESTAMP 
DO
BEGIN
    -- Update rental_fee for rentals older than 2 weeks
    UPDATE Rentals
    SET rental_fee = 20.00
    WHERE DATEDIFF(CURDATE(), received_date) > 14
    AND rental_fee < 20.00; -- Avoids re-updating already updated records
END$$

CREATE TRIGGER ApplyDiscountBeforeInsert
BEFORE INSERT ON Orders
FOR EACH ROW
BEGIN
    DECLARE membership_status BOOLEAN;
    SELECT is_member INTO membership_status
    FROM Customers
    WHERE customer_id = NEW.customer_id;
    -- Apply a 10% discount if the customer is a member
    IF membership_status = TRUE THEN
        SET NEW.total_amount = NEW.total_amount * 0.9;
    END IF;
END$$

CREATE TRIGGER PreventRentalWithoutMembership
BEFORE INSERT ON Orders
FOR EACH ROW
BEGIN
    DECLARE v_is_member BOOLEAN;
    -- Checks if the order is a rental 
    IF NEW.order_type = 'Rental' THEN
        -- Checks if the customer has a membership
        SELECT is_member INTO v_is_member
        FROM Customers
        WHERE customer_id = NEW.customer_id;
        -- If the customer is not a member, raise an error and prevent the rental
        IF NOT v_is_member THEN
            SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'Customer must be a member to rent games';
        END IF;
    END IF;
END$$

CREATE PROCEDURE InsertOrderWithDetails(
    IN in_customer_id INT,
    IN in_employee_id INT,
    IN in_order_type VARCHAR(50),
    IN in_payment_method VARCHAR(50),
    IN in_order_date DATE,
    IN in_game_id INT,
    IN in_quantity INT
)
BEGIN
    DECLARE new_order_id INT;
    DECLARE game_price DECIMAL(10,2);
    DECLARE total DECIMAL(10,2);
    DECLARE current_stock INT;
    SELECT quantity_in_stock INTO current_stock
    FROM Inventory
    WHERE game_id = in_game_id;
    -- Ensures there is enough stock for the order
    IF current_stock < in_quantity THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Not enough stock available for the requested game.';
    END IF;
    SELECT price INTO game_price FROM Games WHERE game_id = in_game_id;
    -- Calculates total (Rentals have a fixed fee)
    IF in_order_type = 'Rental' THEN
        SET total = 6.65;
    ELSE
        SET total = game_price * in_quantity;
    END IF;
    -- Inserts into Orders, OrderDetails, and Payments table
    INSERT INTO Orders (customer_id, order_date, order_type, total_amount)
    VALUES (in_customer_id, in_order_date, in_order_type, total);
    SET new_order_id = LAST_INSERT_ID();
    INSERT INTO OrderDetails (order_id, game_id, employee_id, quantity, unit_price)
    VALUES (new_order_id, in_game_id, in_employee_id, in_quantity, game_price);
    INSERT INTO Payments (order_id, payment_date, payment_method, amount_paid)
    VALUES ( new_order_id, in_order_date, in_payment_method, total);
    -- If Rental, insert into Rentals table
    IF in_order_type = 'Rental' THEN
        INSERT INTO Rentals (customer_id, game_id, employee_id, received_date, rental_fee)
        VALUES (in_customer_id, in_game_id, in_employee_id, in_order_date, game_price);
    END IF;
    -- If an order is a purchase, the quantity is updated
    IF in_order_type = 'Purchase' THEN
        UPDATE Inventory
        SET quantity_in_stock = quantity_in_stock - in_quantity
        WHERE game_id = in_game_id;
    END IF;
END$$

CREATE PROCEDURE SearchGames(IN filterType VARCHAR(20), IN searchValue VARCHAR(100))
BEGIN
    SELECT * FROM GameDetails
    WHERE
        (filterType = 'ID' AND game_id = CAST(searchValue AS UNSIGNED))
        OR (filterType = 'Title' AND title LIKE CONCAT('%', searchValue, '%'))
        OR (filterType = 'Genre' AND genre_name LIKE CONCAT('%', searchValue, '%'))
        OR (filterType = 'Platform' AND platform_name LIKE CONCAT('%', searchValue, '%'));
END$$

CREATE PROCEDURE AddCustomer(
    IN p_first_name VARCHAR(100),
    IN p_last_name VARCHAR(100),
    IN p_email VARCHAR(150),
    IN p_is_member BOOLEAN
)
BEGIN
    INSERT INTO Customers (first_name, last_name, email, is_member)
    VALUES (p_first_name, p_last_name, p_email, p_is_member);
END$$

DELIMITER ;

CREATE OR REPLACE VIEW OrderInformation AS
SELECT 
    o.order_id,
    CONCAT(c.first_name, ' ', c.last_name) AS customer_name,
    o.order_date,
    o.order_type,
    g.title AS game_title,
    CONCAT(e.first_name, ' ', e.last_name) AS employee_name,
	o.total_amount AS total_amount  -- Directly use the modified total_amount from Orders
FROM Orders o
JOIN Customers c ON o.customer_id = c.customer_id
JOIN OrderDetails od ON o.order_id = od.order_id
JOIN Games g ON od.game_id = g.game_id
JOIN Employees e ON od.employee_id = e.employee_id
ORDER BY o.order_id;

CREATE OR REPLACE VIEW GameDetails AS
    SELECT 
        g.game_id,
        g.title,
        ge.genre_name,
        g.year_released,
        p.platform_name,
        i.quantity_in_stock,
        g.price
    FROM
        Games g
            JOIN
        Genres ge ON g.genre_id = ge.genre_id
            JOIN
        Platforms p ON g.platform_id = p.platform_id
            JOIN
        Inventory i ON g.game_id = i.game_id;

-- Creates a view for Top-Selling Games TableView (including both orders and rentals)
CREATE OR REPLACE VIEW TopSellingGames AS
SELECT 
    g.game_id,
    g.title AS game_title,
    g.year_released,
    g.price AS game_price,
    -- Calculates total sales from orders
    COALESCE(SUM(od.quantity), 0) AS total_orders,
    -- Calculates total rentals
    COALESCE(COUNT(r.rental_id), 0) AS total_rentals,
    -- Combines orders and rentals for the total number of sales
    (COALESCE(SUM(od.quantity), 0) + COALESCE(COUNT(r.rental_id), 0)) AS total_sales
FROM Games g
LEFT JOIN OrderDetails od ON g.game_id = od.game_id
LEFT JOIN Rentals r ON g.game_id = r.game_id
GROUP BY g.game_id, g.title, g.year_released, g.price
ORDER BY total_sales DESC;

CREATE OR REPLACE VIEW RentalHistory AS
SELECT 
    r.rental_id, 
    CONCAT(c.first_name, ' ', c.last_name) AS customer_name, 
    g.title AS game_title, 
    r.received_date, 
    r.returned_date
FROM Rentals r
JOIN Customers c ON r.customer_id = c.customer_id
JOIN Games g ON r.game_id = g.game_id;