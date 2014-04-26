-- phpMyAdmin SQL Dump
-- version 3.5.8.2
-- http://www.phpmyadmin.net
--
-- Host: sql313.byethost7.com
-- Generation Time: Apr 26, 2014 at 11:12 AM
-- Server version: 5.6.16-64.2-56
-- PHP Version: 5.3.3

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Database: `b7_14574574_cps630webapp`
--

-- --------------------------------------------------------

--
-- Table structure for table `all_users`
--

CREATE TABLE IF NOT EXISTS `all_users` (
  `mac_address` varchar(12) NOT NULL,
  `gps_latitude` double NOT NULL,
  `gps_longitude` double NOT NULL,
  `user_name` varchar(15) NOT NULL,
  `battery_charge` int(11) NOT NULL,
  `id` int(2) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`mac_address`),
  KEY `id` (`id`)
) ENGINE=MyISAM  DEFAULT CHARSET=latin1 AUTO_INCREMENT=40 ;

--
-- Dumping data for table `all_users`
--

INSERT INTO `all_users` (`mac_address`, `gps_latitude`, `gps_longitude`, `user_name`, `battery_charge`, `id`) VALUES
('28BAB5052516', 43.6585029, -79.3770165, 'madeline amesto', 66, 38),
('a00bbacae6a2', 43.6585012, -79.3769651, 'vlad mt', 99, 39),
('2064327acaab', 43.6585096, -79.3769495, 'andrei jeltyi', 98, 37);

-- --------------------------------------------------------

--
-- Table structure for table `circles`
--

CREATE TABLE IF NOT EXISTS `circles` (
  `created_by_mac_address` varchar(20) NOT NULL,
  `circle_id` int(11) NOT NULL,
  `center_marker_latitude` double NOT NULL,
  `center_marker_longitude` double NOT NULL,
  `radius` double NOT NULL,
  `radius_marker_latitude` double NOT NULL,
  `radius_marker_longitude` double NOT NULL,
  `name` varchar(20) NOT NULL,
  `description` varchar(50) NOT NULL,
  UNIQUE KEY `circle _id` (`circle_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `master_circles`
--

CREATE TABLE IF NOT EXISTS `master_circles` (
  `mac_address` varchar(20) NOT NULL,
  `master_latitude` double NOT NULL,
  `master_longitude` double NOT NULL,
  `master_radius` double NOT NULL,
  `radius_latitude` double NOT NULL,
  `radius_longitude` double NOT NULL,
  `name` varchar(20) NOT NULL,
  `description` varchar(20) NOT NULL,
  `is_enabled` tinyint(1) NOT NULL,
  UNIQUE KEY `mac_address` (`mac_address`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Dumping data for table `master_circles`
--

INSERT INTO `master_circles` (`mac_address`, `master_latitude`, `master_longitude`, `master_radius`, `radius_latitude`, `radius_longitude`, `name`, `description`, `is_enabled`) VALUES
('28BAB5052516', 43.6200136, -79.4750532, 33.823123931884766, 43.62001357865209, -79.47463296353817, 'madeleine', 'Your_guide', 1),
('a00bbacae6a2', 0, 0, 0, 0, 0, 'vlad', 'Your_guide', 0);

-- --------------------------------------------------------

--
-- Table structure for table `messages`
--

CREATE TABLE IF NOT EXISTS `messages` (
  `message_id` int(11) unsigned NOT NULL AUTO_INCREMENT,
  `mac_address` varchar(20) NOT NULL,
  `message` varchar(30) NOT NULL,
  `is_new_message` tinyint(1) NOT NULL,
  PRIMARY KEY (`message_id`)
) ENGINE=MyISAM  DEFAULT CHARSET=latin1 AUTO_INCREMENT=29 ;

--
-- Dumping data for table `messages`
--

INSERT INTO `messages` (`message_id`, `mac_address`, `message`, `is_new_message`) VALUES
(28, 'a00bbacae6a2', 'hello', 1);

-- --------------------------------------------------------

--
-- Table structure for table `users_tracked`
--

CREATE TABLE IF NOT EXISTS `users_tracked` (
  `mac_address_tracked_by` varchar(20) NOT NULL,
  `mac_address_tracked` varchar(20) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Dumping data for table `users_tracked`
--

INSERT INTO `users_tracked` (`mac_address_tracked_by`, `mac_address_tracked`) VALUES
('28BAB5052516', '2064327acaab'),
('28BAB5052516', 'a00bbacae6a2');

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
