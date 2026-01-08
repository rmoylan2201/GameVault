-- Inserting dummy data for Genres
INSERT INTO Genres (genre_name) VALUES
('Action'),
('Adventure'),
('Puzzle'),
('RPG'),
('Shooter'),
('Strategy');

-- Inserting dummy data for Platforms
INSERT INTO Platforms (platform_name) VALUES
('PC'),
('PlayStation'),
('Xbox'),
('Nintendo Switch'),
('Mobile');

-- Inserting dummy data for Games
INSERT INTO Games (title, genre_id, platform_id, year_released, price) VALUES
('Epic Adventure', 2, 1, 2022, 49.99),
('Battle Royale', 5, 2, 2021, 59.99),
('Puzzle Master', 3, 3, 2020, 19.99),
('Fantasy Quest', 4, 4, 2019, 39.99),
('Space Shooter', 5, 1, 2023, 29.99),
('Chess Master', 6, 5, 2021, 9.99);

-- Inserting dummy data for Customers
INSERT INTO Customers (first_name, last_name, email, is_member) VALUES
('John', 'Doe', 'john.doe@example.com', TRUE),
('Jane', 'Smith', 'jane.smith@example.com', FALSE),
('Mike', 'Johnson', 'mike.johnson@example.com', TRUE),
('Emily', 'Williams', 'emily.williams@example.com', FALSE),
('Chris', 'Brown', 'chris.brown@example.com', TRUE);

-- Inserting dummy data for Employees
INSERT INTO Employees (first_name, last_name, hire_date, role, salary) VALUES
('Alice', 'Taylor', '2020-06-15', 'Salesperson', 35000.00),
('Bob', 'Miller', '2019-11-22', 'Manager', 55000.00),
('Charlie', 'Wilson', '2021-04-10', 'Technician', 40000.00),
('Diana', 'Moore', '2022-01-30', 'Salesperson', 36000.00),
('Eve', 'Anderson', '2023-07-01', 'Technician', 42000.00);

-- Inserting dummy data for Orders
INSERT INTO Orders (customer_id, order_date, order_type, total_amount) VALUES
(1, '2022-03-10', 'Purchase', 59.99),
(3, '2021-12-11', 'Rental', 9.99),
(2, '2022-01-15', 'Purchase', 39.99),
(4, '2022-02-22', 'Purchase', 79.99),
(5, '2022-04-05', 'Rental', 14.99);

-- Inserting dummy data for OrderDetails
INSERT INTO OrderDetails (order_id, game_id, employee_id, quantity, unit_price) VALUES
(1, 1, 1, 1, 59.99),
(3, 3, 2, 1, 9.99),
(2, 4, 3, 1, 39.99),
(4, 2, 4, 1, 79.99),
(5, 6, 5, 1, 14.99);

-- Inserting dummy data for Rentals
INSERT INTO Rentals (customer_id, game_id, employee_id, received_date, returned_date, rental_fee) VALUES
(1, 1, 1, '2022-03-10', '2022-03-17', 9.99),
(3, 4, 3, '2022-01-15', '2022-01-22', 14.99),
(5, 6, 5, '2022-04-05', NULL, 19.99);

-- Inserting dummy data for Inventory
INSERT INTO Inventory (game_id, quantity_in_stock, restock_date) VALUES
(1, 10, '2022-02-01'),
(2, 5, '2022-03-01'),
(3, 8, '2022-01-15'),
(4, 15, '2022-04-01'),
(6, 20, '2022-04-10');

-- Inserting dummy data for Payments
INSERT INTO Payments (order_id, payment_date, payment_method, amount_paid) VALUES
(1, '2022-03-10', 'Credit Card', 59.99),
(3, '2021-12-11', 'Cash', 9.99),
(2, '2022-01-15', 'Credit Card', 39.99),
(4, '2022-02-22', 'Debit Card', 79.99),
(5, '2022-04-05', 'Venmo', 14.99);