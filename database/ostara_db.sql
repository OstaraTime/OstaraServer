-- phpMyAdmin SQL Dump
-- version 5.1.1deb5ubuntu1
-- https://www.phpmyadmin.net/
--
-- Host: localhost:3306
-- Generation Time: Nov 08, 2023 at 11:03 AM
-- Server version: 10.6.12-MariaDB-0ubuntu0.22.04.1
-- PHP Version: 8.1.2-1ubuntu2.14

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `ostara_db`
--

-- --------------------------------------------------------

--
-- Table structure for table `client`
--

CREATE TABLE `client` (
  `CLIENT_ID` int(11) NOT NULL,
  `NAME` varchar(40) DEFAULT NULL,
  `LOCATION` varchar(40) DEFAULT NULL,
  `AUTH` char(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `dept`
--

CREATE TABLE `dept` (
  `DEPT_ID` int(11) NOT NULL,
  `NAME` varchar(40) DEFAULT NULL COMMENT 'Name of department'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `event`
--

CREATE TABLE `event` (
  `EVENT_ID` int(11) NOT NULL,
  `PERSON_ID` int(11) NOT NULL,
  `TIMESTAMP` timestamp NOT NULL DEFAULT current_timestamp(),
  `TOKEN_ID` int(11) NOT NULL,
  `EVENT_TYPE` int(11) NOT NULL,
  `CLIENT_ID` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `event_type`
--

CREATE TABLE `event_type` (
  `EVENT_TYPE_ID` int(11) NOT NULL,
  `NAME` varchar(40) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `person`
--

CREATE TABLE `person` (
  `PERSON_ID` int(11) NOT NULL,
  `NAME` varchar(40) DEFAULT NULL COMMENT 'First and last name',
  `DEPT_ID` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `token`
--

CREATE TABLE `token` (
  `TOKEN_ID` int(11) NOT NULL,
  `PERSON_ID` int(11) NOT NULL,
  `TOKEN_TYPE_ID` int(11) NOT NULL,
  `VALUE` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `token_type`
--

CREATE TABLE `token_type` (
  `TOKEN_TYPE_ID` int(11) NOT NULL,
  `NAME` varchar(40) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `user`
--

CREATE TABLE `user` (
  `USER_ID` int(11) NOT NULL,
  `USERNAME` varchar(40) NOT NULL,
  `USER_PERSON` int(11) NOT NULL,
  `USER_PASS` varchar(40) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `client`
--
ALTER TABLE `client`
  ADD PRIMARY KEY (`CLIENT_ID`);

--
-- Indexes for table `dept`
--
ALTER TABLE `dept`
  ADD PRIMARY KEY (`DEPT_ID`);

--
-- Indexes for table `event`
--
ALTER TABLE `event`
  ADD PRIMARY KEY (`EVENT_ID`),
  ADD KEY `EVENT_PERSON` (`PERSON_ID`),
  ADD KEY `EVENT_TOKEN` (`TOKEN_ID`),
  ADD KEY `EVENT_CLIENT` (`CLIENT_ID`),
  ADD KEY `EVENT_EVENT_TYPE` (`EVENT_TYPE`);

--
-- Indexes for table `event_type`
--
ALTER TABLE `event_type`
  ADD PRIMARY KEY (`EVENT_TYPE_ID`);

--
-- Indexes for table `person`
--
ALTER TABLE `person`
  ADD PRIMARY KEY (`PERSON_ID`),
  ADD KEY `PERSON_DEPT` (`DEPT_ID`);

--
-- Indexes for table `token`
--
ALTER TABLE `token`
  ADD PRIMARY KEY (`TOKEN_ID`),
  ADD KEY `TOKEN_TYPE_TOKEN` (`TOKEN_TYPE_ID`),
  ADD KEY `TOKEN_PERSON` (`PERSON_ID`);

--
-- Indexes for table `token_type`
--
ALTER TABLE `token_type`
  ADD PRIMARY KEY (`TOKEN_TYPE_ID`);

--
-- Indexes for table `user`
--
ALTER TABLE `user`
  ADD PRIMARY KEY (`USER_ID`),
  ADD UNIQUE KEY `USERNAME` (`USERNAME`),
  ADD KEY `USER_PERSON` (`USER_PERSON`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `client`
--
ALTER TABLE `client`
  MODIFY `CLIENT_ID` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `dept`
--
ALTER TABLE `dept`
  MODIFY `DEPT_ID` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `event`
--
ALTER TABLE `event`
  MODIFY `EVENT_ID` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `event_type`
--
ALTER TABLE `event_type`
  MODIFY `EVENT_TYPE_ID` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `person`
--
ALTER TABLE `person`
  MODIFY `PERSON_ID` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `token`
--
ALTER TABLE `token`
  MODIFY `TOKEN_ID` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `token_type`
--
ALTER TABLE `token_type`
  MODIFY `TOKEN_TYPE_ID` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `user`
--
ALTER TABLE `user`
  MODIFY `USER_ID` int(11) NOT NULL AUTO_INCREMENT;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `event`
--
ALTER TABLE `event`
  ADD CONSTRAINT `EVENT_CLIENT` FOREIGN KEY (`CLIENT_ID`) REFERENCES `client` (`CLIENT_ID`),
  ADD CONSTRAINT `EVENT_EVENT_TYPE` FOREIGN KEY (`EVENT_TYPE`) REFERENCES `event_type` (`EVENT_TYPE_ID`),
  ADD CONSTRAINT `EVENT_PERSON` FOREIGN KEY (`PERSON_ID`) REFERENCES `person` (`PERSON_ID`),
  ADD CONSTRAINT `EVENT_TOKEN` FOREIGN KEY (`TOKEN_ID`) REFERENCES `token` (`TOKEN_ID`);

--
-- Constraints for table `person`
--
ALTER TABLE `person`
  ADD CONSTRAINT `PERSON_DEPT` FOREIGN KEY (`DEPT_ID`) REFERENCES `dept` (`DEPT_ID`);

--
-- Constraints for table `token`
--
ALTER TABLE `token`
  ADD CONSTRAINT `TOKEN_PERSON` FOREIGN KEY (`PERSON_ID`) REFERENCES `person` (`PERSON_ID`),
  ADD CONSTRAINT `TOKEN_TYPE_TOKEN` FOREIGN KEY (`TOKEN_TYPE_ID`) REFERENCES `token_type` (`TOKEN_TYPE_ID`);

--
-- Constraints for table `user`
--
ALTER TABLE `user`
  ADD CONSTRAINT `USER_PERSON` FOREIGN KEY (`USER_PERSON`) REFERENCES `person` (`PERSON_ID`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
