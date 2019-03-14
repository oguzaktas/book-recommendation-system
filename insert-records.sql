load data infile '/var/lib/mysql-files/BX-Users.csv' into table `BX-Users` character set latin1 fields terminated by ';' enclosed by '"' lines terminated by '\r\n' ignore 1 lines (`User-ID`, Location, `Age`);

load data infile '/var/lib/mysql-files/BX-Books.csv' into table `BX-Books` character set latin1 fields terminated by ';' enclosed by '"' lines terminated by '\r\n' ignore 1 lines (ISBN, `Book-Title`, `Book-Author`, `Year-Of-Publication`, Publisher, `Image-URL-S`, `Image-URL-M`, `Image-URL-L`);

load data infile '/var/lib/mysql-files/BX-Book-Ratings.csv' into table `BX-Book-Ratings` character set latin1 fields terminated by ';' enclosed by '"' lines terminated by '\r\n' ignore 1 lines (`User-ID`, ISBN, `Book-Rating`);

UPDATE `BX-Users` SET Age = FLOOR(20 + RAND() * 10) WHERE Age IS NULL;

