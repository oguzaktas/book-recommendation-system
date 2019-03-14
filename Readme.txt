150201170 - Mehmet Oğuz Aktaş
150201245 - Ebubakir Şit

Kitap Okuma ve Öneri Sistemi

* Uygulamanın çalışabilmesi için öncelikle book_crossing isminde veritabanının, MySQL Workbench (veya farklı bir MySQL veritabanı yönetim uygulaması) ile create-db-tables.sql dosyası kullanılarak oluşturulması ve insert-records.sql dosyası ile veritabanına kayıtların eklenmesi gerekmektedir. insert-records.sql dosyasında CSV dosyalarından kayıtları veritabanına import edebilmek için,

load data infile '/var/lib/mysql-files/BX-Users.csv' into table `BX-Users` character set latin1 fields terminated by ';' enclosed by '"' lines terminated by '\r\n' ignore 1 lines (`User-ID`, Location, `Age`);

load data infile '/var/lib/mysql-files/BX-Books.csv' into table `BX-Books` character set latin1 fields terminated by ';' enclosed by '"' lines terminated by '\r\n' ignore 1 lines (ISBN, `Book-Title`, `Book-Author`, `Year-Of-Publication`, Publisher, `Image-URL-S`, `Image-URL-M`, `Image-URL-L`);

load data infile '/var/lib/mysql-files/BX-Book-Ratings.csv' into table `BX-Book-Ratings` character set latin1 fields terminated by ';' enclosed by '"' lines terminated by '\r\n' ignore 1 lines (`User-ID`, ISBN, `Book-Rating`);

komutları kullanıldı. /var/lib/mysql-files şeklinde olan dosya yolu farklıysa değiştirilmelidir.

* MySQL ile Java bağlantısı sağlayabilmek için MySQL Connector Java kütüphanesinin projeye dahil edilmesi gerekmektedir. Projeye dahil etmek için yapılması gereken adımlar şunlardır;

- NetBeans IDE veya kullanılan farklı IDE üzerinden proje özellikleri açılır.
- Libraries bölümünden "Add JAR/Folder" seçeneği ile mysql-connector-java-8.0.13.jar dosyası seçilir.

* PDF'den okuma işlemleri için gerekli olan ICEpdf 6.3.1 kütüphanesi, java.icepdf klasöründeki .jar dosyaları ile üstteki adımlar takip edilerek projeye dahil edilmelidir.

* MySQL ayarları yapıldıktan sonra tüm GUI classlarında veritabanı ile bağlantı kurulmasını sağlayan getConnection() metodu içerisinde yazılan veritabanı ismi, MySQL kullanıcı adı ve şifresi duruma göre değiştirilmelidir.

Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/book_crossing?useSSL=false", "root", "123456");

Üstteki kodda veritabanı ismi book_crossing, MySQL kullanıcısı root ve MySQL şifresi olarak 123456 kullanılmıştır. Bunlar farklıysa değiştirilmelidir.
