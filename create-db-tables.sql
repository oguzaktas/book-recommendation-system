-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema book_crossing
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema book_crossing
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `book_crossing` DEFAULT CHARACTER SET latin1 ;
USE `book_crossing` ;

-- -----------------------------------------------------
-- Table `book_crossing`.`BX-Users`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `book_crossing`.`BX-Users` (
  `User-ID` INT(11) NOT NULL,
  `Location` VARCHAR(255) DEFAULT NULL,
  `Age` INT(3) DEFAULT NULL,
  `Password` VARCHAR(25) NOT NULL DEFAULT '123456',
  PRIMARY KEY (`User-ID`),
  UNIQUE INDEX `User-ID_UNIQUE` (`User-ID` ASC) VISIBLE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `book_crossing`.`BX-Books`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `book_crossing`.`BX-Books` (
  `ISBN` VARCHAR(13) BINARY NOT NULL,
  `Book-Title` VARCHAR(255) DEFAULT NULL,
  `Book-Author` VARCHAR(255) NULL DEFAULT NULL,
  `Year-Of-Publication` INT(4) UNSIGNED DEFAULT NULL,
  `Publisher` VARCHAR(255) DEFAULT NULL,
  `Image-URL-S` VARCHAR(255) BINARY DEFAULT NULL,
  `Image-URL-M` VARCHAR(255) BINARY DEFAULT NULL,
  `Image-URL-L` VARCHAR(255) BINARY DEFAULT NULL,
  `Insertion-Date` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`ISBN`),
  UNIQUE INDEX `ISBN_UNIQUE` (`ISBN` ASC) VISIBLE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `book_crossing`.`BX-Book-Ratings`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `book_crossing`.`BX-Book-Ratings` (
  `User-ID` INT(11) NOT NULL,
  `ISBN` VARCHAR(13) BINARY NOT NULL,
  `Book-Rating` INT(2) NOT NULL DEFAULT 0,
  PRIMARY KEY (`User-ID`, `ISBN`),
  INDEX `fk_BX-Book-Ratings_BX-Users_idx` (`User-ID` ASC) VISIBLE,
  CONSTRAINT `fk_BX-Book-Ratings_BX-Users`
    FOREIGN KEY (`User-ID`)
    REFERENCES `book_crossing`.`BX-Users` (`User-ID`)
    ON DELETE NO ACTION
    ON UPDATE CASCADE)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `book_crossing`.`BX-Admin`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `book_crossing`.`BX-Admin` (
  `ID` INT NOT NULL AUTO_INCREMENT,
  `Username` VARCHAR(45) NOT NULL,
  `Password` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`ID`, `Username`),
  UNIQUE INDEX `ID_UNIQUE` (`ID` ASC) VISIBLE,
  UNIQUE INDEX `Username_UNIQUE` (`Username` ASC) VISIBLE)
ENGINE = InnoDB;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;

