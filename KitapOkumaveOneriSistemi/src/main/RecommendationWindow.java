
package main;

import java.awt.Color;
import java.awt.Image;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;
import org.icepdf.ri.common.SwingController;
import org.icepdf.ri.common.SwingViewBuilder;

/**
 * Yazilim Laboratuvari I Proje 1
 * @author Oguz Aktas
 */
public class RecommendationWindow extends javax.swing.JFrame {
    
    private final String id;
    
    private ArrayList<Entry<Integer, Double>> similarusers;
        
    private final int agemargine = 1;
    
    private String filepath;

    /**
     * Creates new form RecommendationWindow
     * @param id
     * @throws java.sql.SQLException
     * @throws java.lang.ClassNotFoundException
     */
    public RecommendationWindow(String id) throws SQLException, ClassNotFoundException {
        initComponents();
        this.setLocationRelativeTo(null);
        this.setTitle("Kitap Okuma ve Oneri Sistemi");
        this.id = id;
        showValues();
        findSimilarity();
        tablodaGoster();
    }
    
    public Connection getConnection() throws SQLException, ClassNotFoundException {
        Connection conn;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/book_crossing?useSSL=false", "root", "123456");
            return conn;
        } catch (SQLException ex) {
            Logger.getLogger(RecommendationWindow.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, "<html><b>Veritabanina baglanti saglanamadi</b></html>");
        }
        return null;
    }
    
    public final void showValues() throws SQLException, ClassNotFoundException {
        text_uyeid.setText(id);
        text_uyeid.setBackground(Color.YELLOW);
        text_uyeid.setEditable(false);
    }
    
    public final void findSimilarity() throws SQLException, ClassNotFoundException {
        HashMap<String, Integer> user = new HashMap<String, Integer>();
        Connection conn = getConnection();
        String query = "SELECT * FROM `BX-Book-Ratings` WHERE `User-ID` = " + id + ";";
        Statement st;
        ResultSet rs;
        try {
            st = conn.createStatement();
            rs = st.executeQuery(query);
            while (rs.next()) {
                user.put(rs.getString("ISBN"), rs.getInt("Book-Rating"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(RecommendationWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
        HashMap<Integer, Double> userdistance = new HashMap<Integer, Double>();
        query = "SELECT * FROM `BX-Book-Ratings` br1, `BX-Book-Ratings` br2 WHERE br1.`User-ID` != " + id + " AND br2.`User-ID` = " + id + " AND br1.ISBN = br2.ISBN;";
        try {
            st = conn.createStatement();
            rs = st.executeQuery(query);
            int first, next;
            while (rs.next()) {
                first = rs.getInt("br1.Book-Rating");
                next = rs.getInt("br2.Book-Rating");
                userdistance.put(rs.getInt("br1.User-ID"), userdistance.getOrDefault(rs.getInt("br1.User-ID"), 0.0) + Math.pow(first - next, 2));
            }
        } catch (SQLException ex) {
            Logger.getLogger(RecommendationWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
        similarusers = new ArrayList<Entry<Integer, Double>>(userdistance.entrySet());
        similarusers.sort(Entry.comparingByValue());
    }
    
    public ArrayList<String> getReadISBN() throws SQLException, ClassNotFoundException {
        ArrayList<String> readbooks = new ArrayList<String>();
        Connection conn = getConnection();
        String query = "SELECT ISBN FROM `BX-Book-Ratings` WHERE `User-ID` = " + id + ";";
        Statement st;
        ResultSet rs;
        try {
            st = conn.createStatement();
            rs = st.executeQuery(query);
            String isbn;
            while (rs.next()) {
                isbn = rs.getString("ISBN");
                readbooks.add(isbn);
            }
        } catch (SQLException ex) {
            Logger.getLogger(RecommendationWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
        return readbooks;
    }
    
    public ArrayList<Books> recommendBook(int quantity) throws SQLException, ClassNotFoundException { // User-user collaborative filtering Euclidian distance yontemi kullanildi.
        ArrayList<Books> recommendedbooks = new ArrayList<Books>();
        ArrayList<String> readbooks = new ArrayList<String>();
        int count = 0;
        Connection conn = getConnection();
        String query = "SELECT ISBN FROM `BX-Book-Ratings` WHERE `User-ID` = " + id + ";";
        Statement st;
        ResultSet rs;
        try {
            st = conn.createStatement();
            rs = st.executeQuery(query);
            String isbn;
            while (rs.next()) {
                isbn = rs.getString("ISBN");
                readbooks.add(isbn);
            }
            for (Entry<Integer, Double> e : similarusers) {
                query = "SELECT * FROM `BX-Book-Ratings` WHERE `User-ID` = " + e.getKey() + ";";
                try {
                    st = conn.createStatement();
                    rs = st.executeQuery(query);
                    while (rs.next()) {
                        isbn = rs.getString("ISBN");
                        if (!readbooks.contains(isbn) && rs.getInt("Book-Rating") > 7) {
                            query = "SELECT * FROM `BX-Books` WHERE ISBN = '" + isbn + "';";
                            try {
                                st = conn.createStatement();
                                rs = st.executeQuery(query);
                                Books book;
                                while (rs.next()) {
                                    if (count < quantity) {
                                        book = new Books(rs.getString("ISBN"), rs.getString("Book-Title"), rs.getString("Book-Author"), rs.getInt("Year-Of-Publication"), rs.getString("Publisher"), rs.getString("Image-URL-M"));
                                        recommendedbooks.add(book);
                                        count++;
                                    } else {
                                        return recommendedbooks;
                                    }
                                }
                            } catch (SQLException ex) {
                                Logger.getLogger(RecommendationWindow.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(RecommendationWindow.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(RecommendationWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
        return recommendedbooks;
    }
    
    /*
    public ArrayList<String> getFilteredBooksISBN() throws SQLException, ClassNotFoundException { // User-user collaborative filtering temel mantigi kullanildi.
        ArrayList<String> recommendedbooks = new ArrayList<String>();
        ArrayList<String> bestratedbooks = new ArrayList<String>();
        ArrayList<String> collaborativeusers = new ArrayList<String>();
        Connection conn = getConnection();
        String query1 = "SELECT ISBN FROM `BX-Book-Ratings` WHERE `Book-Rating` = (SELECT MAX(`Book-Rating`) FROM `BX-Book-Ratings` WHERE `User-ID` = " + id + ") AND `User-ID` = " + id + ";";
        String query2 = "SELECT ISBN FROM `BX-Book-Ratings` WHERE `Book-Rating` = (SELECT MAX(`Book-Rating`)-1 FROM `BX-Book-Ratings` WHERE `User-ID` = " + id + ") AND `User-ID` = " + id + ";";
        String query3 = "SELECT ISBN FROM `BX-Book-Ratings` WHERE `Book-Rating` = (SELECT MAX(`Book-Rating`)-2 FROM `BX-Book-Ratings` WHERE `User-ID` = " + id + ") AND `User-ID` = " + id + ";";
        Statement st;
        ResultSet rs;
        try {
            st = conn.createStatement();
            rs = st.executeQuery(query1);
            String isbn;
            while (rs.next()) {
                isbn = rs.getString("ISBN");
                bestratedbooks.add(isbn);
            }
            rs = st.executeQuery(query2);
            while (rs.next()) {
                isbn = rs.getString("ISBN");
                bestratedbooks.add(isbn);
            }
            rs = st.executeQuery(query3);
            while (rs.next()) {
                isbn = rs.getString("ISBN");
                bestratedbooks.add(isbn);
            }
        } catch (SQLException ex) {
            Logger.getLogger(RecommendationWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
        for (int i = 0; i < bestratedbooks.size(); i++) {
            String query4 = "SELECT `User-ID` FROM `BX-Book-Ratings` WHERE `Book-Rating` >= 6 AND ISBN = '" + bestratedbooks.get(i) + "' AND `User-ID` != " + id + ";";
            try {
                st = conn.createStatement();
                rs = st.executeQuery(query4);
                String userid;
                while (rs.next()) {
                    userid = rs.getString("User-ID");
                    collaborativeusers.add(userid);
                }
            } catch (SQLException ex) {
                Logger.getLogger(RecommendationWindow.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        for (int j = 0; j < collaborativeusers.size(); j++) {
            String query5 = "SELECT ISBN FROM `BX-Book-Ratings` WHERE `Book-Rating` = (SELECT MAX(`Book-Rating`) FROM `BX-Book-Ratings` WHERE `User-ID` = " + collaborativeusers.get(j) + ") AND `User-ID` = " + collaborativeusers.get(j) + ";";
            try {
                st = conn.createStatement();
                rs = st.executeQuery(query5);
                String isbn;
                while (rs.next()) {
                    isbn = rs.getString("ISBN");
                    if (bestratedbooks.contains(isbn)) {
                        query5 = "SELECT ISBN FROM `BX-Book-Ratings` WHERE `Book-Rating` = (SELECT MAX(`Book-Rating`) FROM `BX-Book-Ratings` WHERE `User-ID` = " + collaborativeusers.get(j) + " AND ISBN != '" + isbn + "') AND `User-ID` = " + collaborativeusers.get(j) + ";";
                        try {
                            st = conn.createStatement();
                            rs = st.executeQuery(query5);
                            while (rs.next()) {
                                isbn = rs.getString("ISBN");
                            }
                        } catch (SQLException ex) {
                            Logger.getLogger(RecommendationWindow.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    recommendedbooks.add(isbn);
                }
            } catch (SQLException ex) {
                Logger.getLogger(RecommendationWindow.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return recommendedbooks;
    }
    */
    
    public ArrayList<String> getBooksInAgeGroupISBN() throws SQLException, ClassNotFoundException { // Ayni yas araligindaki uyeler arasinda en begenilen 2 kitap (ortalama ratingi en yuksek olan kitaplarin en cok oy alani) collaborative filtering mantigi kullanilarak bulundu.
        ArrayList<String> isbninagegroup = new ArrayList<String>();
        ArrayList<String> readbooks = getReadISBN(); 
        Connection conn = getConnection();
        int age = 0;
        String query1 = "SELECT Age FROM `BX-Users` WHERE `User-ID` = " + id + ";";
        Statement st;
        ResultSet rs;
        try {
            st = conn.createStatement();
            rs = st.executeQuery(query1);
            while (rs.next()) {
                age = rs.getInt("Age");
            }
        } catch (SQLException ex) {
            Logger.getLogger(RecommendationWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
        String query2 = "SELECT ISBN, AVG(`Book-Rating`) FROM `BX-Book-Ratings` WHERE `User-ID` IN (SELECT `User-ID` FROM `BX-Users` WHERE Age >= " + (age - agemargine) + " AND Age <= " + (age + agemargine) + ") GROUP BY ISBN ORDER BY AVG(`Book-Rating`) DESC, COUNT(ISBN) DESC LIMIT 2;";
        try {
            st = conn.createStatement();
            rs = st.executeQuery(query2);
            String isbn;
            while (rs.next()) {
                isbn = rs.getString("ISBN");
                if (!readbooks.contains(isbn)) {
                    isbninagegroup.add(isbn);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(RecommendationWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
        return isbninagegroup;
    }
    
    public ArrayList<String> getBooksLocationISBN() throws SQLException, ClassNotFoundException { // Ayni konumdaki uyeler arasinda en begenilen kitap (ortalama ratingi en yuksek olan kitaplarin en cok oy alani) collaborative filtering mantigi kullanilarak bulundu.
        ArrayList<String> isbnlocation = new ArrayList<String>();
        ArrayList<String> readbooks = getReadISBN();
        Connection conn = getConnection();
        String location = null;
        String query1 = "SELECT Location FROM `BX-Users` WHERE `User-ID` = " + id + ";";
        Statement st;
        ResultSet rs;
        try {
            st = conn.createStatement();
            rs = st.executeQuery(query1);
            while (rs.next()) {
                location = rs.getString("Location");
            }
        } catch (SQLException ex) {
            Logger.getLogger(RecommendationWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
        String query2 = "SELECT ISBN, AVG(`Book-Rating`) FROM `BX-Book-Ratings` WHERE `User-ID` IN (SELECT `User-ID` FROM `BX-Users` WHERE Location = '" + location + "') GROUP BY ISBN ORDER BY AVG(`Book-Rating`) DESC, COUNT(ISBN) DESC LIMIT 1;";
        try {
            st = conn.createStatement();
            rs = st.executeQuery(query2);
            String isbn;
            while (rs.next()) {
                isbn = rs.getString("ISBN");
                if (!readbooks.contains(isbn)) {
                    isbnlocation.add(isbn);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(RecommendationWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
        return isbnlocation;
    }
    
    public ArrayList<Books> getRecommendedBookList() throws SQLException, ClassNotFoundException { // 12 kitap recommendBook() metodu ile, en fazla 2 kitap getBooksInAgeGroupISBN() metodu ile, en fazla 1 kitap da getBooksLocationISBN() metodu ile alinarak (metodlar arasinda ayni kitap bulunursa 1 kez eklenecek, hic kitap bulunamazsa eklenmeyecek ve bulunan kitap uyenin oy verdigi kitaplar arasindaysa eklenmeyecek kosullari saglanarak) oneri listesine eklendi.
        ArrayList<Books> recommendedbooks = new ArrayList<Books>();
        ArrayList <Books> mainrecommends = recommendBook(12);
        recommendedbooks.addAll(mainrecommends);
        ArrayList<String> isbnlist2 = getBooksInAgeGroupISBN();
        ArrayList<String> isbnlist3 = getBooksLocationISBN();
        Connection conn = getConnection();
        String query = "SELECT * FROM `BX-Books` WHERE ISBN = ?;";
        PreparedStatement ps;
        ResultSet rs;
        try {
            for (int i = 0; i < isbnlist2.size(); i++) {
                ps = conn.prepareStatement(query);
                ps.setString(1, isbnlist2.get(i));
                rs = ps.executeQuery();
                Books book;
                while (rs.next()) {
                    book = new Books(rs.getString("ISBN"), rs.getString("Book-Title"), rs.getString("Book-Author"), rs.getInt("Year-Of-Publication"), rs.getString("Publisher"), rs.getString("Image-URL-M"));
                    if (!recommendedbooks.contains(book)) {
                        recommendedbooks.add(book);
                    }
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(RecommendationWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            for (int i = 0; i < isbnlist3.size(); i++) {
                ps = conn.prepareStatement(query);
                ps.setString(1, isbnlist3.get(i));
                rs = ps.executeQuery();
                Books book;
                while (rs.next()) {
                    book = new Books(rs.getString("ISBN"), rs.getString("Book-Title"), rs.getString("Book-Author"), rs.getInt("Year-Of-Publication"), rs.getString("Publisher"), rs.getString("Image-URL-M"));
                    if (!recommendedbooks.contains(book)) {
                        recommendedbooks.add(book);
                    }
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(RecommendationWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
        return recommendedbooks;
    }
    
    public final void tablodaGoster() throws SQLException, ClassNotFoundException {
        ArrayList<Books> list = getRecommendedBookList();
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        model.setRowCount(0);
        Object[] kayit = new Object[5];
        for (int i = 0; i < list.size(); i++) {
            kayit[0] = list.get(i).getISBN();
            kayit[1] = list.get(i).getTitle();
            kayit[2] = list.get(i).getAuthor();
            kayit[3] = list.get(i).getYear();
            kayit[4] = list.get(i).getPublisher();
            model.addRow(kayit);
        }
    }
    
    public void showItem(int index) throws SQLException, ClassNotFoundException, MalformedURLException, IOException {
        ArrayList<Books> list = getRecommendedBookList();
        text_isbn.setText(list.get(index).getISBN());
        text_title.setText(list.get(index).getTitle());
        text_author.setText(list.get(index).getAuthor());
        text_year.setText(Integer.toString(list.get(index).getYear()));
        text_publisher.setText(list.get(index).getPublisher());
        
        try {
            URL url = new URL(list.get(index).getImgurlmedium());
            Image image = ImageIO.read(url);
            label_imageurl.setIcon(new ImageIcon(image));
        } catch (MalformedURLException ex) {
            Logger.getLogger(RecommendationWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void readBook() throws IOException {
        SwingController controller = new SwingController();
        SwingViewBuilder factory = new SwingViewBuilder(controller);
        JPanel viewer = factory.buildViewerPanel();
        controller.getDocumentViewController().setAnnotationCallback(new org.icepdf.ri.common.MyAnnotationCallback(controller.getDocumentViewController()));
        JFrame appframe = new JFrame();
        appframe.add(viewer);
        Random rand = new Random();
        int random = rand.nextInt(13) + 1;
        switch (random) {
            case 1:
                filepath = "src/java/books/daniel-defoe-robinson-crusoe.pdf";
                controller.openDocument(filepath);
                controller.getDocumentViewController().setZoom((float) 1.7);
                appframe.pack();
                appframe.setLocationRelativeTo(null);
                appframe.setTitle("Kitap Okuma ve Oneri Sistemi");
                appframe.setVisible(true);
                break;
            case 2:
                filepath = "src/java/books/franz-kafka-the-metamorphosis.pdf";
                controller.openDocument(filepath);
                controller.getDocumentViewController().setZoom((float) 1.7);
                appframe.pack();
                appframe.setLocationRelativeTo(null);
                appframe.setTitle("Kitap Okuma ve Oneri Sistemi");
                appframe.setVisible(true);
                break;
            case 3:
                filepath = "src/java/books/franz-kafka-the-trial.pdf";
                controller.openDocument(filepath);
                controller.getDocumentViewController().setZoom((float) 1.7);
                appframe.pack();
                appframe.setLocationRelativeTo(null);
                appframe.setTitle("Kitap Okuma ve Oneri Sistemi");
                appframe.setVisible(true);
                break;
            case 4:
                filepath = "src/java/books/fyodor-dostoyevsky-crime-and-punishment.pdf";
                controller.openDocument(filepath);
                controller.getDocumentViewController().setZoom((float) 1.7);
                appframe.pack();
                appframe.setLocationRelativeTo(null);
                appframe.setTitle("Kitap Okuma ve Oneri Sistemi");
                appframe.setVisible(true);
                break;
            case 5:
                filepath = "src/java/books/fyodor-dostoyevsky-notes-from-the-underground.pdf";
                controller.openDocument(filepath);
                controller.getDocumentViewController().setZoom((float) 1.7);
                appframe.pack();
                appframe.setLocationRelativeTo(null);
                appframe.setTitle("Kitap Okuma ve Oneri Sistemi");
                appframe.setVisible(true);
                break;
            case 6:
                filepath = "src/java/books/fyodor-dostoyevsky-the-brothers-karamazov.pdf";
                controller.openDocument(filepath);
                controller.getDocumentViewController().setZoom((float) 1.7);
                appframe.pack();
                appframe.setLocationRelativeTo(null);
                appframe.setTitle("Kitap Okuma ve Oneri Sistemi");
                appframe.setVisible(true);
                break;
            case 7:
                filepath = "src/java/books/george-orwell-1984.pdf";
                controller.openDocument(filepath);
                controller.getDocumentViewController().setZoom((float) 1.7);
                appframe.pack();
                appframe.setLocationRelativeTo(null);
                appframe.setTitle("Kitap Okuma ve Oneri Sistemi");
                appframe.setVisible(true);
                break;
            case 8:
                filepath = "src/java/books/homer-the-iliad.pdf";
                controller.openDocument(filepath);
                controller.getDocumentViewController().setZoom((float) 1.7);
                appframe.pack();
                appframe.setLocationRelativeTo(null);
                appframe.setTitle("Kitap Okuma ve Oneri Sistemi");
                appframe.setVisible(true);
                break;
            case 9:
                filepath = "src/java/books/homer-the-odyssey.pdf";
                controller.openDocument(filepath);
                controller.getDocumentViewController().setZoom((float) 1.7);
                appframe.pack();
                appframe.setLocationRelativeTo(null);
                appframe.setTitle("Kitap Okuma ve Oneri Sistemi");
                appframe.setVisible(true);
                break;
            case 10:
                filepath = "src/java/books/jules-verne-around-the-world-in-80-days.pdf";
                controller.openDocument(filepath);
                controller.getDocumentViewController().setZoom((float) 1.7);
                appframe.pack();
                appframe.setLocationRelativeTo(null);
                appframe.setTitle("Kitap Okuma ve Oneri Sistemi");
                appframe.setVisible(true);
                break;
            case 11:
                filepath = "src/java/books/leo-tolstoy-anna-karenina.pdf";
                controller.openDocument(filepath);
                controller.getDocumentViewController().setZoom((float) 1.7);
                appframe.pack();
                appframe.setLocationRelativeTo(null);
                appframe.setTitle("Kitap Okuma ve Oneri Sistemi");
                appframe.setVisible(true);
                break;
            case 12:
                filepath = "src/java/books/lewis-carroll-alices-adventures-in-wonderland.pdf";
                controller.openDocument(filepath);
                controller.getDocumentViewController().setZoom((float) 1.7);
                appframe.pack();
                appframe.setLocationRelativeTo(null);
                appframe.setTitle("Kitap Okuma ve Oneri Sistemi");
                appframe.setVisible(true);
                break;
            case 13:
                filepath = "src/java/books/mary-shelley-frankenstein.pdf";
                controller.openDocument(filepath);
                controller.getDocumentViewController().setZoom((float) 1.7);
                appframe.pack();
                appframe.setLocationRelativeTo(null);
                appframe.setTitle("Kitap Okuma ve Oneri Sistemi");
                appframe.setVisible(true);
                break;
            default:
                break;
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        text_uyeid = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        label_id = new javax.swing.JLabel();
        label_image = new javax.swing.JLabel();
        label_imageurl = new javax.swing.JLabel();
        label_isbn = new javax.swing.JLabel();
        text_isbn = new javax.swing.JTextField();
        label_title = new javax.swing.JLabel();
        text_title = new javax.swing.JTextField();
        text_author = new javax.swing.JTextField();
        label_author = new javax.swing.JLabel();
        label_publisher = new javax.swing.JLabel();
        text_publisher = new javax.swing.JTextField();
        Btn_Back = new javax.swing.JButton();
        Btn_Logout = new javax.swing.JButton();
        label_year = new javax.swing.JLabel();
        text_year = new javax.swing.JTextField();
        Btn_Oku = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(51, 204, 255));

        text_uyeid.setBackground(new java.awt.Color(51, 204, 255));
        text_uyeid.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N

        jTable1.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "ISBN", "Book Title", "Author", "Year", "Publisher"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable1MouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTable1);

        label_id.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        label_id.setText("Kullanici ID : ");

        label_image.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        label_image.setText("Image : ");

        label_isbn.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        label_isbn.setText("ISBN : ");

        text_isbn.setEditable(false);
        text_isbn.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        text_isbn.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        text_isbn.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        text_isbn.setPreferredSize(new java.awt.Dimension(69, 39));
        text_isbn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                text_isbnActionPerformed(evt);
            }
        });

        label_title.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        label_title.setText("Kitap : ");

        text_title.setEditable(false);
        text_title.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        text_title.setPreferredSize(new java.awt.Dimension(69, 39));
        text_title.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                text_titleActionPerformed(evt);
            }
        });

        text_author.setEditable(false);
        text_author.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        text_author.setPreferredSize(new java.awt.Dimension(69, 39));
        text_author.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                text_authorActionPerformed(evt);
            }
        });

        label_author.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        label_author.setText("Yazar : ");

        label_publisher.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        label_publisher.setText("Yayinevi : ");

        text_publisher.setEditable(false);
        text_publisher.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        text_publisher.setPreferredSize(new java.awt.Dimension(69, 39));
        text_publisher.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                text_publisherActionPerformed(evt);
            }
        });

        Btn_Back.setFont(new java.awt.Font("Tahoma", 1, 19)); // NOI18N
        Btn_Back.setIcon(new javax.swing.ImageIcon(getClass().getResource("/java/icons/back.png"))); // NOI18N
        Btn_Back.setText(" Geri Don");
        Btn_Back.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Btn_BackActionPerformed(evt);
            }
        });

        Btn_Logout.setFont(new java.awt.Font("Tahoma", 1, 19)); // NOI18N
        Btn_Logout.setIcon(new javax.swing.ImageIcon(getClass().getResource("/java/icons/logout.png"))); // NOI18N
        Btn_Logout.setText("Cikis Yap");
        Btn_Logout.setIconTextGap(10);
        Btn_Logout.setPreferredSize(new java.awt.Dimension(180, 51));
        Btn_Logout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Btn_LogoutActionPerformed(evt);
            }
        });

        label_year.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        label_year.setText("Yayin Yili : ");

        text_year.setEditable(false);
        text_year.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        text_year.setPreferredSize(new java.awt.Dimension(69, 39));
        text_year.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                text_yearActionPerformed(evt);
            }
        });

        Btn_Oku.setFont(new java.awt.Font("Tahoma", 1, 19)); // NOI18N
        Btn_Oku.setIcon(new javax.swing.ImageIcon(getClass().getResource("/java/icons/read.png"))); // NOI18N
        Btn_Oku.setText(" Kitabi Oku");
        Btn_Oku.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Btn_OkuActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(274, 274, 274)
                .addComponent(label_id)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(text_uyeid, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(40, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(Btn_Back, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(87, 87, 87)
                        .addComponent(Btn_Logout, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(181, 181, 181))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 776, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(30, 30, 30)))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(label_author)
                            .addComponent(label_year)
                            .addComponent(label_title)
                            .addComponent(label_isbn)
                            .addComponent(label_publisher))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(text_year, javax.swing.GroupLayout.DEFAULT_SIZE, 148, Short.MAX_VALUE)
                                .addComponent(text_author, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(text_title, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(text_isbn, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(text_publisher, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(30, 30, 30))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(label_image)
                        .addGap(18, 18, 18)
                        .addComponent(label_imageurl, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(26, 26, 26))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(Btn_Oku, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(55, 55, 55))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label_id)
                    .addComponent(text_uyeid, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(29, 29, 29)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 635, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(text_isbn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_isbn))
                        .addGap(26, 26, 26)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(text_title, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_title))
                        .addGap(26, 26, 26)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(text_author, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_author))
                        .addGap(25, 25, 25)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(text_year, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_year))
                        .addGap(24, 24, 24)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(text_publisher, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_publisher))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(27, 27, 27)
                                .addComponent(label_imageurl, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(111, 111, 111)
                                .addComponent(label_image)))
                        .addGap(50, 50, 50)
                        .addComponent(Btn_Oku, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 54, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Btn_Back, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Btn_Logout, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(48, 48, 48))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        int index = jTable1.getSelectedRow();
        try {
            showItem(index);
        } catch (SQLException | ClassNotFoundException | IOException ex) {
            Logger.getLogger(RecommendationWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jTable1MouseClicked

    private void text_isbnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_text_isbnActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_text_isbnActionPerformed

    private void text_titleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_text_titleActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_text_titleActionPerformed

    private void text_authorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_text_authorActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_text_authorActionPerformed

    private void text_publisherActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_text_publisherActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_text_publisherActionPerformed

    private void Btn_BackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Btn_BackActionPerformed
        this.dispose();
        try {
            new UserWindow(text_uyeid.getText()).setVisible(true);
        } catch (SQLException | ClassNotFoundException ex) {
            Logger.getLogger(RecommendationWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_Btn_BackActionPerformed

    private void Btn_LogoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Btn_LogoutActionPerformed
        this.dispose();
        new LoginWindow().setVisible(true);
    }//GEN-LAST:event_Btn_LogoutActionPerformed

    private void text_yearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_text_yearActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_text_yearActionPerformed

    private void Btn_OkuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Btn_OkuActionPerformed
        if (text_isbn.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "<html><b>Okumak icin bir kitap seciniz.</b></html>");
        } else {
            try {
                readBook();
            } catch (IOException ex) {
                Logger.getLogger(RecommendationWindow.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_Btn_OkuActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Btn_Back;
    private javax.swing.JButton Btn_Logout;
    private javax.swing.JButton Btn_Oku;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JLabel label_author;
    private javax.swing.JLabel label_id;
    private javax.swing.JLabel label_image;
    private javax.swing.JLabel label_imageurl;
    private javax.swing.JLabel label_isbn;
    private javax.swing.JLabel label_publisher;
    private javax.swing.JLabel label_title;
    private javax.swing.JLabel label_year;
    private javax.swing.JTextField text_author;
    private javax.swing.JTextField text_isbn;
    private javax.swing.JTextField text_publisher;
    private javax.swing.JTextField text_title;
    private javax.swing.JTextField text_uyeid;
    private javax.swing.JTextField text_year;
    // End of variables declaration//GEN-END:variables
}
