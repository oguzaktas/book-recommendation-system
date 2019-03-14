
package main;

import com.sun.glass.events.KeyEvent;
import java.awt.Color;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
public class UserWindow extends javax.swing.JFrame {
    
    private final String id;
    
    private boolean clicked = false;
    private String filepath;

    /**
     * Creates new form UserWindow
     * @param id
     * @throws java.sql.SQLException
     * @throws java.lang.ClassNotFoundException
     */
    public UserWindow(String id) throws SQLException, ClassNotFoundException {
        initComponents();
        this.setLocationRelativeTo(null);
        this.setTitle("Kitap Okuma ve Oneri Sistemi");
        this.id = id;
        showValues();
        tablodaGoster();
    }
    
    public Connection getConnection() throws SQLException, ClassNotFoundException {
        Connection conn;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/book_crossing?useSSL=false", "root", "123456");
            return conn;
        } catch (SQLException ex) {
            Logger.getLogger(UserWindow.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, "<html><b>Veritabanina baglanti saglanamadi</b></html>");
        }
        return null;
    }
    
    public final void showValues() throws SQLException, ClassNotFoundException {
        text_uyeid.setText(id);
        text_uyeid.setBackground(Color.YELLOW);
        text_uyeid.setEditable(false);
    }
    
    public ArrayList<Books> getBookList() throws SQLException, ClassNotFoundException {
        ArrayList<Books> booklist = new ArrayList<Books>();
        Connection conn = getConnection();
        String query = "SELECT * FROM `BX-Books`;";
        Statement st;
        ResultSet rs;
        try {
            st = conn.createStatement();
            rs = st.executeQuery(query);
            Books book;
            while (rs.next()) {
                book = new Books(rs.getString("ISBN"), rs.getString("Book-Title"), rs.getString("Book-Author"), rs.getInt("Year-Of-Publication"), rs.getString("Publisher"), rs.getString("Image-URL-M"));
                booklist.add(book);
            }
        } catch (SQLException ex) {
            Logger.getLogger(UserWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
        return booklist;
    }
    
    public ArrayList<Books> getNewBookList() throws SQLException, ClassNotFoundException {
        ArrayList<Books> booklist = new ArrayList<Books>();
        Connection conn = getConnection();
        String query = "SELECT * FROM `BX-Books` ORDER BY `Insertion-Date` DESC LIMIT 5;";
        Statement st;
        ResultSet rs;
        try {
            st = conn.createStatement();
            rs = st.executeQuery(query);
            Books book;
            while (rs.next()) {
                book = new Books(rs.getString("ISBN"), rs.getString("Book-Title"), rs.getString("Book-Author"), rs.getInt("Year-Of-Publication"), rs.getString("Publisher"), rs.getString("Image-URL-M"));
                booklist.add(book);
            }
        } catch (SQLException ex) {
            Logger.getLogger(UserWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
        return booklist;
    }
    
    public final void tablodaGoster() throws SQLException, ClassNotFoundException {
        ArrayList<Books> booklist = getBookList();
        ArrayList<Books> newbooklist = getNewBookList();
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        model.setRowCount(0);
        Object[] kayit = new Object[5];
        for (int i=0; i < booklist.size(); i++) {
            kayit[0] = booklist.get(i).getISBN();
            kayit[1] = booklist.get(i).getTitle();
            kayit[2] = booklist.get(i).getAuthor();
            kayit[3] = booklist.get(i).getYear();
            kayit[4] = booklist.get(i).getPublisher();
            model.addRow(kayit);
        }
        model = (DefaultTableModel) jTable2.getModel();
        model.setRowCount(0);
        for (int i=0; i < newbooklist.size(); i++) {
            kayit[0] = newbooklist.get(i).getISBN();
            kayit[1] = newbooklist.get(i).getTitle();
            kayit[2] = newbooklist.get(i).getAuthor();
            kayit[3] = newbooklist.get(i).getYear();
            kayit[4] = newbooklist.get(i).getPublisher();
            model.addRow(kayit);
        }
    }
    
    public void showItem1(int index) throws SQLException, ClassNotFoundException, MalformedURLException, IOException {
        ArrayList<Books> list = getBookList();
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
            Logger.getLogger(UserWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void showItem2(int index) throws SQLException, ClassNotFoundException, MalformedURLException, IOException {
        ArrayList<Books> newbooklist = getNewBookList();
        text_isbn.setText(newbooklist.get(index).getISBN());
        text_title.setText(newbooklist.get(index).getTitle());
        text_author.setText(newbooklist.get(index).getAuthor());
        text_year.setText(Integer.toString(newbooklist.get(index).getYear()));
        text_publisher.setText(newbooklist.get(index).getPublisher());

        try {
            URL url = new URL(newbooklist.get(index).getImgurlmedium());
            Image image = ImageIO.read(url);
            label_imageurl.setIcon(new ImageIcon(image));
        } catch (MalformedURLException ex) {
            Logger.getLogger(UserWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void showItem3(int index) throws SQLException, ClassNotFoundException, MalformedURLException, IOException {
        ArrayList<Books> list = searchBooks(text_ara.getText());
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
            Logger.getLogger(UserWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public boolean checkInputs() {
        if (text_isbn.getText().trim().isEmpty()) {
            return false;
        } else {
            return true;
        }
    }
    
    public boolean checkRating(String id, String isbn) throws SQLException, ClassNotFoundException {
        Connection conn = getConnection();
        String query = "SELECT * FROM `BX-Book-Ratings` WHERE `User-ID` = '" + id + "' AND ISBN = '" + isbn + "';";
        Statement st;
        ResultSet rs;
        boolean rated = false;
        try {
            st = conn.createStatement();
            rs = st.executeQuery(query);
            if (rs.next()) {
                rated = true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(UserWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
        return rated;
    }
    
    public ArrayList<Books> searchBooks(String value) throws SQLException, ClassNotFoundException {
        ArrayList<Books> list = new ArrayList<Books>();
        Connection conn = getConnection();
        String query = "SELECT * FROM `BX-Books` WHERE CONCAT(ISBN, `Book-Title`, `Book-Author`, `Year-Of-Publication`, Publisher, `Image-URL-M`) LIKE '%" + value + "%' COLLATE utf8mb4_unicode_ci;";
        Statement st;
        ResultSet rs;
        try {
            st = conn.createStatement();
            rs = st.executeQuery(query);
            Books book;
            while (rs.next()) {
                book = new Books(rs.getString("ISBN"), rs.getString("Book-Title"), rs.getString("Book-Author"), rs.getInt("Year-Of-Publication"), rs.getString("Publisher"), rs.getString("Image-URL-M"));
                list.add(book);
            }
        } catch (SQLException ex) {
            Logger.getLogger(UserWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
        return list;
    }
    
    public void filteredtablodaGoster() throws SQLException, ClassNotFoundException {
        ArrayList<Books> list = searchBooks(text_ara.getText());
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
        label_tumkitaplar = new javax.swing.JLabel();
        text_uyeid = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        label_id = new javax.swing.JLabel();
        label_image = new javax.swing.JLabel();
        label_rating = new javax.swing.JLabel();
        combobox_rating = new javax.swing.JComboBox<>();
        label_imageurl = new javax.swing.JLabel();
        label_isbn = new javax.swing.JLabel();
        text_isbn = new javax.swing.JTextField();
        label_title = new javax.swing.JLabel();
        text_title = new javax.swing.JTextField();
        text_author = new javax.swing.JTextField();
        label_author = new javax.swing.JLabel();
        label_publisher = new javax.swing.JLabel();
        text_publisher = new javax.swing.JTextField();
        Btn_Istatistik = new javax.swing.JButton();
        label_year = new javax.swing.JLabel();
        text_year = new javax.swing.JTextField();
        Btn_Oy = new javax.swing.JButton();
        Btn_Oku = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        label_yenikitaplar = new javax.swing.JLabel();
        Btn_Logout = new javax.swing.JButton();
        text_ara = new javax.swing.JTextField();
        Btn_Ara = new javax.swing.JButton();
        Btn_Oneri = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(51, 204, 255));

        label_tumkitaplar.setFont(new java.awt.Font("Tahoma", 1, 21)); // NOI18N
        label_tumkitaplar.setText("Tum Kitaplar : ");
        label_tumkitaplar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                label_tumkitaplarMouseClicked(evt);
            }
        });

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

        label_rating.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        label_rating.setText("Oyunuz : ");

        combobox_rating.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        combobox_rating.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10" }));
        combobox_rating.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                combobox_ratingActionPerformed(evt);
            }
        });

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

        Btn_Istatistik.setFont(new java.awt.Font("Tahoma", 1, 19)); // NOI18N
        Btn_Istatistik.setIcon(new javax.swing.ImageIcon(getClass().getResource("/java/icons/statistics.png"))); // NOI18N
        Btn_Istatistik.setText(" Istatistikler");
        Btn_Istatistik.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Btn_IstatistikActionPerformed(evt);
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

        Btn_Oy.setFont(new java.awt.Font("Tahoma", 1, 19)); // NOI18N
        Btn_Oy.setIcon(new javax.swing.ImageIcon(getClass().getResource("/java/icons/rate.png"))); // NOI18N
        Btn_Oy.setText(" Oy Ver");
        Btn_Oy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Btn_OyActionPerformed(evt);
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

        jTable2.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jTable2.setModel(new javax.swing.table.DefaultTableModel(
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
        jTable2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable2MouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(jTable2);

        label_yenikitaplar.setFont(new java.awt.Font("Tahoma", 1, 21)); // NOI18N
        label_yenikitaplar.setText("Yeni Kitaplar : ");
        label_yenikitaplar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                label_yenikitaplarMouseClicked(evt);
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

        text_ara.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        text_ara.setPreferredSize(new java.awt.Dimension(69, 39));
        text_ara.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                text_araActionPerformed(evt);
            }
        });
        text_ara.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                text_araKeyPressed(evt);
            }
        });

        Btn_Ara.setFont(new java.awt.Font("Tahoma", 1, 19)); // NOI18N
        Btn_Ara.setIcon(new javax.swing.ImageIcon(getClass().getResource("/java/icons/search.png"))); // NOI18N
        Btn_Ara.setText(" Ara");
        Btn_Ara.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Btn_AraActionPerformed(evt);
            }
        });

        Btn_Oneri.setFont(new java.awt.Font("Tahoma", 1, 19)); // NOI18N
        Btn_Oneri.setIcon(new javax.swing.ImageIcon(getClass().getResource("/java/icons/recommendation.png"))); // NOI18N
        Btn_Oneri.setText(" Onerilen Kitaplar");
        Btn_Oneri.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Btn_OneriActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(38, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 727, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 727, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_yenikitaplar)
                            .addComponent(label_tumkitaplar))
                        .addGap(26, 26, 26))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(text_ara, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(54, 54, 54)
                        .addComponent(Btn_Ara, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(191, 191, 191)))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(label_publisher)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(text_publisher, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(label_author)
                                    .addComponent(label_year)
                                    .addComponent(label_title)
                                    .addComponent(label_isbn))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(text_year, javax.swing.GroupLayout.DEFAULT_SIZE, 148, Short.MAX_VALUE)
                                        .addComponent(text_author, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(text_title, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                    .addComponent(text_isbn, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(30, 30, 30))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(label_image)
                        .addGap(18, 18, 18)
                        .addComponent(label_imageurl, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(26, 26, 26))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(Btn_Oku, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addGap(12, 12, 12)
                                    .addComponent(Btn_Oy, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                    .addComponent(label_rating)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(combobox_rating, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(16, 16, 16))))
                        .addGap(57, 57, 57))))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(274, 274, 274)
                        .addComponent(label_id)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(text_uyeid, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(112, 112, 112)
                        .addComponent(Btn_Istatistik, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(109, 109, 109)
                        .addComponent(Btn_Oneri, javax.swing.GroupLayout.PREFERRED_SIZE, 251, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(106, 106, 106)
                        .addComponent(Btn_Logout, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(label_id)
                    .addComponent(text_uyeid, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addComponent(label_tumkitaplar, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 460, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(text_ara, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(Btn_Ara, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(10, 10, 10)
                        .addComponent(label_yenikitaplar, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(41, 41, 41)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(Btn_Istatistik, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(Btn_Logout, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(Btn_Oneri, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(text_isbn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_isbn))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(text_title, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_title))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(text_author, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_author))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(text_year, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_year))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(text_publisher, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_publisher))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addComponent(label_imageurl, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(91, 91, 91)
                                .addComponent(label_image)))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(141, 141, 141)
                                .addComponent(Btn_Oku, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(combobox_rating, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(label_rating))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(Btn_Oy, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(49, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void label_tumkitaplarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_label_tumkitaplarMouseClicked

    }//GEN-LAST:event_label_tumkitaplarMouseClicked

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        jTable2.getSelectionModel().clearSelection();
        int index = jTable1.getSelectedRow();
        Btn_Ara.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == Btn_Ara) {
                    clicked = true;
                }
            }
        });
        if (clicked == false) {
            try {
                showItem1(index);
            } catch (SQLException | ClassNotFoundException | IOException ex) {
                Logger.getLogger(UserWindow.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            try {
                showItem3(index);
            } catch (SQLException | ClassNotFoundException | IOException ex) {
                Logger.getLogger(UserWindow.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_jTable1MouseClicked

    private void combobox_ratingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_combobox_ratingActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_combobox_ratingActionPerformed

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

    private void Btn_IstatistikActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Btn_IstatistikActionPerformed
        this.dispose();
        try {
            new StatisticsWindow(text_uyeid.getText()).setVisible(true);
        } catch (SQLException | ClassNotFoundException ex) {
            Logger.getLogger(UserWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_Btn_IstatistikActionPerformed

    private void text_yearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_text_yearActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_text_yearActionPerformed

    private void Btn_OyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Btn_OyActionPerformed
        if (checkInputs()) {
            try {
                if (checkRating(text_uyeid.getText(), text_isbn.getText())) {
                    JOptionPane.showMessageDialog(null, "<html><b>Bu kitabi zaten oyladiniz.</b></html>");
                } else {
                    Connection conn = null;
                    try {
                        conn = getConnection();
                    } catch (SQLException | ClassNotFoundException ex) {
                        Logger.getLogger(UserWindow.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    PreparedStatement ps;
                    try {
                        ps = conn.prepareStatement("INSERT INTO `BX-Book-Ratings`(`User-ID`, ISBN, `Book-Rating`) VALUES(?, ?, ?);");
                        ps.setString(1, text_uyeid.getText());
                        ps.setString(2, text_isbn.getText());
                        ps.setString(3, combobox_rating.getSelectedItem().toString());
                        ps.executeUpdate();
                    } catch (SQLException ex) {
                        Logger.getLogger(UserWindow.class.getName()).log(Level.SEVERE, null, ex);
                        JOptionPane.showMessageDialog(null, "<html><b>Oy verme islemi sirasinda hata olustu.</b></html>");
                    }
                    JOptionPane.showMessageDialog(null, "<html><b>Oyunuz basariyla kaydedilmistir.</b></html>");
                }
            } catch (SQLException | ClassNotFoundException ex) {
                Logger.getLogger(UserWindow.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            JOptionPane.showMessageDialog(null, "<html><b>Oy vermek icin bir kitap seciniz.</b></html>");
        }
    }//GEN-LAST:event_Btn_OyActionPerformed

    private void Btn_OkuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Btn_OkuActionPerformed
        if (text_isbn.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "<html><b>Okumak icin bir kitap seciniz.</b></html>");
        } else {
            try {
                readBook();
            } catch (IOException ex) {
                Logger.getLogger(UserWindow.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_Btn_OkuActionPerformed

    private void label_yenikitaplarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_label_yenikitaplarMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_label_yenikitaplarMouseClicked

    private void jTable2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable2MouseClicked
        jTable1.getSelectionModel().clearSelection();
        int index = jTable2.getSelectedRow();
        try {
            showItem2(index);
        } catch (SQLException | ClassNotFoundException | IOException ex) {
            Logger.getLogger(UserWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jTable2MouseClicked

    private void Btn_LogoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Btn_LogoutActionPerformed
        this.dispose();
        new LoginWindow().setVisible(true);
    }//GEN-LAST:event_Btn_LogoutActionPerformed

    private void text_araActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_text_araActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_text_araActionPerformed

    private void Btn_AraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Btn_AraActionPerformed
        try {
            filteredtablodaGoster();
        } catch (SQLException | ClassNotFoundException ex) {
            Logger.getLogger(UserWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_Btn_AraActionPerformed

    private void text_araKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_text_araKeyPressed
        int key = evt.getKeyCode();
        if (key == KeyEvent.VK_ENTER) {
            clicked = true;
            try {
                filteredtablodaGoster();
            } catch (SQLException | ClassNotFoundException ex) {
                Logger.getLogger(UserWindow.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_text_araKeyPressed

    private void Btn_OneriActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Btn_OneriActionPerformed
        this.dispose();
        try {
            new RecommendationWindow(text_uyeid.getText()).setVisible(true);
        } catch (SQLException | ClassNotFoundException ex) {
            Logger.getLogger(UserWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_Btn_OneriActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Btn_Ara;
    private javax.swing.JButton Btn_Istatistik;
    private javax.swing.JButton Btn_Logout;
    private javax.swing.JButton Btn_Oku;
    private javax.swing.JButton Btn_Oneri;
    private javax.swing.JButton Btn_Oy;
    private javax.swing.JComboBox<String> combobox_rating;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    private javax.swing.JLabel label_author;
    private javax.swing.JLabel label_id;
    private javax.swing.JLabel label_image;
    private javax.swing.JLabel label_imageurl;
    private javax.swing.JLabel label_isbn;
    private javax.swing.JLabel label_publisher;
    private javax.swing.JLabel label_rating;
    private javax.swing.JLabel label_title;
    private javax.swing.JLabel label_tumkitaplar;
    private javax.swing.JLabel label_year;
    private javax.swing.JLabel label_yenikitaplar;
    private javax.swing.JTextField text_ara;
    private javax.swing.JTextField text_author;
    private javax.swing.JTextField text_isbn;
    private javax.swing.JTextField text_publisher;
    private javax.swing.JTextField text_title;
    private javax.swing.JTextField text_uyeid;
    private javax.swing.JTextField text_year;
    // End of variables declaration//GEN-END:variables
}
