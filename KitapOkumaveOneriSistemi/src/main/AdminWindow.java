
package main;

import java.awt.Color;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 * Yazilim Laboratuvari I Proje 1
 * @author Oguz Aktas
 */
public class AdminWindow extends javax.swing.JFrame {
    
    private final String username;
    
    private boolean clicked1 = false;
    private boolean clicked2 = false;

    /**
     * Creates new form AdminWindow
     * @param username
     * @throws java.sql.SQLException
     * @throws java.lang.ClassNotFoundException
     */
    public AdminWindow(String username) throws SQLException, ClassNotFoundException {
        initComponents();
        this.setLocationRelativeTo(null);
        this.setTitle("Kitap Okuma ve Oneri Sistemi");
        this.username = username;
        showValues();
    }
    
    public Connection getConnection() throws SQLException, ClassNotFoundException {
        Connection conn;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/book_crossing?useSSL=false", "root", "123456");
            return conn;
        } catch (SQLException ex) {
            Logger.getLogger(AdminWindow.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, "<html><b>Veritabanina baglanti saglanamadi</b></html>");
        }
        return null;
    }
    
    public final void showValues() {
        text_username.setText(username);
        text_username.setBackground(Color.YELLOW);
        text_username.setEditable(false);
        jFrame1.setEnabled(false);
        jFrame2.setEnabled(false);
        jFrame3.setEnabled(false);
    }

    public ArrayList<Users> getUserList() throws SQLException, ClassNotFoundException {
        ArrayList<Users> userlist = new ArrayList<Users>();
        Connection conn = getConnection();
        String query = "SELECT * FROM `BX-Users`;";
        Statement st;
        ResultSet rs;
        try {
            st = conn.createStatement();
            rs = st.executeQuery(query);
            Users user;
            while (rs.next()) {
                user = new Users(rs.getInt("User-ID"), rs.getString("Location"), rs.getInt("Age"), rs.getString("Password"));
                userlist.add(user);
            }
        } catch (SQLException ex) {
            Logger.getLogger(AdminWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
        return userlist;
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
            Logger.getLogger(AdminWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
        return booklist;
    }
    
    public ArrayList<Admins> getAdminList() throws SQLException, ClassNotFoundException {
        ArrayList<Admins> adminlist = new ArrayList<Admins>();
        Connection conn = getConnection();
        String query = "SELECT * FROM `BX-Admin`;";
        Statement st;
        ResultSet rs;
        try {
            st = conn.createStatement();
            rs = st.executeQuery(query);
            Admins admin;
            while (rs.next()) {
                admin = new Admins(rs.getInt("ID"), rs.getString("Username"), rs.getString("Password"));
                adminlist.add(admin);
            }
        } catch (SQLException ex) {
            Logger.getLogger(AdminWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
        return adminlist;
    }

    public void tablodaGoster1() throws SQLException, ClassNotFoundException {
        ArrayList<Users> list = getUserList();
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        model.setRowCount(0);
        Object[] kayit = new Object[4];
        for (int i=0; i < list.size(); i++) {
            kayit[0] = list.get(i).getId();
            kayit[1] = list.get(i).getLocation();
            kayit[2] = list.get(i).getAge();
            kayit[3] = list.get(i).getPassword();
            model.addRow(kayit);
        }
    }
    
    public void tablodaGoster2() throws SQLException, ClassNotFoundException {
        ArrayList<Books> list = getBookList();
        DefaultTableModel model = (DefaultTableModel) jTable2.getModel();
        model.setRowCount(0);
        Object[] kayit = new Object[5];
        for (int i=0; i < list.size(); i++) {
            kayit[0] = list.get(i).getISBN();
            kayit[1] = list.get(i).getTitle();
            kayit[2] = list.get(i).getAuthor();
            kayit[3] = list.get(i).getYear();
            kayit[4] = list.get(i).getPublisher();
            model.addRow(kayit);
        }
    }
    
    public void tablodaGoster3() throws SQLException, ClassNotFoundException {
        ArrayList<Admins> list = getAdminList();
        DefaultTableModel model = (DefaultTableModel) jTable3.getModel();
        model.setRowCount(0);
        Object[] kayit = new Object[3];
        for (int i=0; i < list.size(); i++) {
            kayit[0] = list.get(i).getId();
            kayit[1] = list.get(i).getUsername();
            kayit[2] = list.get(i).getPassword();
            model.addRow(kayit);
        }
    }
    
    public ArrayList<Users> searchUsers(String value) throws SQLException, ClassNotFoundException {
        ArrayList<Users> list = new ArrayList<Users>();
        Connection conn = getConnection();
        String query = "SELECT * FROM `BX-Users` WHERE CONCAT(`User-ID`, Location, Age, Password) LIKE '%" + value + "%' COLLATE utf8mb4_unicode_ci;";
        Statement st;
        ResultSet rs;
        try {
            st = conn.createStatement();
            rs = st.executeQuery(query);
            Users user;
            while (rs.next()) {
                user = new Users(rs.getInt("User-ID"), rs.getString("Location"), rs.getInt("Age"), rs.getString("Password"));
                list.add(user);
            }
        } catch (SQLException ex) {
            Logger.getLogger(AdminWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
        return list;
    }
    
    public void filteredUserstablodaGoster() throws SQLException, ClassNotFoundException {
        ArrayList<Users> list = searchUsers(text_ara.getText());
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        model.setRowCount(0);
        Object[] kayit = new Object[4];
        for (int i = 0; i < list.size(); i++) {
            kayit[0] = list.get(i).getId();
            kayit[1] = list.get(i).getLocation();
            kayit[2] = list.get(i).getAge();
            kayit[3] = list.get(i).getPassword();
            model.addRow(kayit);
        }
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
            Logger.getLogger(AdminWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
        return list;
    }
    
    public void filteredBookstablodaGoster() throws SQLException, ClassNotFoundException {
        ArrayList<Books> list = searchBooks(text_ara1.getText());
        DefaultTableModel model = (DefaultTableModel) jTable2.getModel();
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
    
    public void showItem1(int index) throws SQLException, ClassNotFoundException, MalformedURLException, IOException {
        ArrayList<Users> list = getUserList();
        text_userid.setText(Integer.toString(list.get(index).getId()));
        text_location.setText(list.get(index).getLocation());
        text_age.setText(Integer.toString(list.get(index).getAge()));
        text_userpassword.setText(list.get(index).getPassword());
    }
    
    public void showItem2(int index) throws SQLException, ClassNotFoundException, MalformedURLException, IOException {
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
            Logger.getLogger(AdminWindow.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, "<html><b>Resim URL'si gecerli bir adres degil.</b></html>");
        }
        
        text_imageurl.setText(list.get(index).getImgurlmedium());
    }
    
    public void showItem3(int index) throws SQLException, ClassNotFoundException, MalformedURLException, IOException {
        ArrayList<Admins> list = getAdminList();
        text_adminid.setText(Integer.toString(list.get(index).getId()));
        text_adminusername.setText(list.get(index).getUsername());
        text_adminpassword.setText(list.get(index).getPassword());
    }
    
    public void showItem4(int index) throws SQLException, ClassNotFoundException, MalformedURLException, IOException {
        ArrayList<Users> list = searchUsers(text_ara.getText());
        text_userid.setText(Integer.toString(list.get(index).getId()));
        text_location.setText(list.get(index).getLocation());
        text_age.setText(Integer.toString(list.get(index).getAge()));
        text_userpassword.setText(list.get(index).getPassword());
    }
    
    public void showItem5(int index) throws SQLException, ClassNotFoundException, MalformedURLException, IOException {
        ArrayList<Books> list = searchBooks(text_ara1.getText());
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
            Logger.getLogger(AdminWindow.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, "<html><b>Resim URL'si gecerli bir adres degil.</b></html>");
        }
        
        text_imageurl.setText(list.get(index).getImgurlmedium());
    }
        
    public boolean checkInputs1() {
        if (text_userid.getText().trim().isEmpty() || text_location.getText().trim().isEmpty() || text_age.getText().trim().isEmpty() || text_userpassword.getText().trim().isEmpty()) {
            return false;
        } else {
            return true;
        }
    }
    
    public boolean checkInputs2() {
        if (text_isbn.getText().trim().isEmpty() || text_title.getText().trim().isEmpty() || text_author.getText().trim().isEmpty() || text_year.getText().trim().isEmpty() || text_publisher.getText().trim().isEmpty() || text_imageurl.getText().trim().isEmpty()) {
            return false;
        } else {
            return true;
        }
    }
    
    public boolean checkInputs3() {
        if (text_adminid.getText().trim().isEmpty() || text_adminusername.getText().trim().isEmpty() || text_adminpassword.getText().trim().isEmpty()) {
            return false;
        } else {
            return true;
        }
    }
    
    public boolean checkID(String id) throws SQLException, ClassNotFoundException {
        Connection conn = getConnection();
        String query = "SELECT * FROM `BX-Users` WHERE `User-ID` = '" + id + "';";
        Statement st;
        ResultSet rs;
        boolean used = false;
        try {
            st = conn.createStatement();
            rs = st.executeQuery(query);
            if (rs.next()) {
                used = true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(AdminWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
        return used;
    }
    
    public boolean checkISBN(String isbn) throws SQLException, ClassNotFoundException {
        Connection conn = getConnection();
        String query = "SELECT * FROM `BX-Books` WHERE ISBN = '" + isbn + "';";
        Statement st;
        ResultSet rs;
        boolean used = false;
        try {
            st = conn.createStatement();
            rs = st.executeQuery(query);
            if (rs.next()) {
                used = true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(AdminWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
        return used;
    }
    
    public boolean checkAdminID(String id) throws SQLException, ClassNotFoundException {
        Connection conn = getConnection();
        String query = "SELECT * FROM `BX-Admin` WHERE ID = '" + id + "';";
        Statement st;
        ResultSet rs;
        boolean used = false;
        try {
            st = conn.createStatement();
            rs = st.executeQuery(query);
            if (rs.next()) {
                used = true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(AdminWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
        return used;
    }
    
    public boolean checkAdminUsername(String username) throws SQLException, ClassNotFoundException {
        Connection conn = getConnection();
        String query = "SELECT * FROM `BX-Admin` WHERE Username = '" + username + "';";
        Statement st;
        ResultSet rs;
        boolean used = false;
        try {
            st = conn.createStatement();
            rs = st.executeQuery(query);
            if (rs.next()) {
                used = true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(AdminWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
        return used;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jFrame1 = new javax.swing.JFrame();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        label_userid = new javax.swing.JLabel();
        text_userid = new javax.swing.JTextField();
        label_location = new javax.swing.JLabel();
        text_location = new javax.swing.JTextField();
        text_age = new javax.swing.JTextField();
        label_age = new javax.swing.JLabel();
        label_userpassword = new javax.swing.JLabel();
        text_userpassword = new javax.swing.JTextField();
        text_ara = new javax.swing.JTextField();
        Btn_Ara = new javax.swing.JButton();
        Btn_Back = new javax.swing.JButton();
        Btn_Logout1 = new javax.swing.JButton();
        Btn_Insert = new javax.swing.JButton();
        Btn_Update = new javax.swing.JButton();
        Btn_Delete = new javax.swing.JButton();
        jFrame2 = new javax.swing.JFrame();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
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
        label_year = new javax.swing.JLabel();
        text_year = new javax.swing.JTextField();
        text_ara1 = new javax.swing.JTextField();
        Btn_Ara1 = new javax.swing.JButton();
        label_imageurledit = new javax.swing.JLabel();
        text_imageurl = new javax.swing.JTextField();
        Btn_Back1 = new javax.swing.JButton();
        Btn_Logout2 = new javax.swing.JButton();
        Btn_Insert1 = new javax.swing.JButton();
        Btn_Update1 = new javax.swing.JButton();
        Btn_Delete1 = new javax.swing.JButton();
        jFrame3 = new javax.swing.JFrame();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTable3 = new javax.swing.JTable();
        label_imageurl2 = new javax.swing.JLabel();
        label_adminid = new javax.swing.JLabel();
        text_adminid = new javax.swing.JTextField();
        text_adminusername = new javax.swing.JTextField();
        label_username = new javax.swing.JLabel();
        label_adminpassword = new javax.swing.JLabel();
        text_adminpassword = new javax.swing.JTextField();
        Btn_Back2 = new javax.swing.JButton();
        Btn_Logout3 = new javax.swing.JButton();
        Btn_Insert2 = new javax.swing.JButton();
        Btn_Update2 = new javax.swing.JButton();
        Btn_Delete2 = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        Btn_Books = new javax.swing.JButton();
        Btn_Users = new javax.swing.JButton();
        Btn_Admins = new javax.swing.JButton();
        Btn_Logout = new javax.swing.JButton();
        label_welcome = new javax.swing.JLabel();
        text_username = new javax.swing.JTextField();

        jPanel2.setBackground(new java.awt.Color(51, 204, 255));

        jTable1.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "User ID", "Location", "Age", "Password"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
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

        label_userid.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        label_userid.setText("User ID : ");

        text_userid.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        text_userid.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        text_userid.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        text_userid.setPreferredSize(new java.awt.Dimension(69, 39));
        text_userid.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                text_useridActionPerformed(evt);
            }
        });

        label_location.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        label_location.setText("Konum : ");

        text_location.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        text_location.setPreferredSize(new java.awt.Dimension(69, 39));
        text_location.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                text_locationActionPerformed(evt);
            }
        });

        text_age.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        text_age.setPreferredSize(new java.awt.Dimension(69, 39));
        text_age.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                text_ageActionPerformed(evt);
            }
        });

        label_age.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        label_age.setText("Yas : ");

        label_userpassword.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        label_userpassword.setText("Sifre : ");

        text_userpassword.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        text_userpassword.setPreferredSize(new java.awt.Dimension(69, 39));
        text_userpassword.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                text_userpasswordActionPerformed(evt);
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

        Btn_Back.setFont(new java.awt.Font("Tahoma", 1, 19)); // NOI18N
        Btn_Back.setIcon(new javax.swing.ImageIcon(getClass().getResource("/java/icons/back.png"))); // NOI18N
        Btn_Back.setText(" Geri Don");
        Btn_Back.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Btn_BackActionPerformed(evt);
            }
        });

        Btn_Logout1.setFont(new java.awt.Font("Tahoma", 1, 19)); // NOI18N
        Btn_Logout1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/java/icons/logout.png"))); // NOI18N
        Btn_Logout1.setText("Cikis Yap");
        Btn_Logout1.setIconTextGap(10);
        Btn_Logout1.setPreferredSize(new java.awt.Dimension(180, 51));
        Btn_Logout1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Btn_Logout1ActionPerformed(evt);
            }
        });

        Btn_Insert.setFont(new java.awt.Font("Tahoma", 1, 19)); // NOI18N
        Btn_Insert.setIcon(new javax.swing.ImageIcon(getClass().getResource("/java/icons/insert.png"))); // NOI18N
        Btn_Insert.setText(" Insert");
        Btn_Insert.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Btn_InsertActionPerformed(evt);
            }
        });

        Btn_Update.setFont(new java.awt.Font("Tahoma", 1, 19)); // NOI18N
        Btn_Update.setIcon(new javax.swing.ImageIcon(getClass().getResource("/java/icons/update.png"))); // NOI18N
        Btn_Update.setText(" Update");
        Btn_Update.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Btn_UpdateActionPerformed(evt);
            }
        });

        Btn_Delete.setFont(new java.awt.Font("Tahoma", 1, 19)); // NOI18N
        Btn_Delete.setIcon(new javax.swing.ImageIcon(getClass().getResource("/java/icons/delete.png"))); // NOI18N
        Btn_Delete.setText(" Delete");
        Btn_Delete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Btn_DeleteActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addComponent(text_ara, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(54, 54, 54))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addComponent(Btn_Back, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(99, 99, 99)))
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(Btn_Logout1, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(Btn_Ara, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(151, 151, 151))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(44, 44, 44)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 727, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 55, Short.MAX_VALUE)))
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(label_age)
                            .addComponent(label_userpassword)
                            .addComponent(label_location)
                            .addComponent(label_userid))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(text_userpassword, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(text_age, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(text_location, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(text_userid, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(36, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(Btn_Insert, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(Btn_Update, javax.swing.GroupLayout.DEFAULT_SIZE, 161, Short.MAX_VALUE)
                            .addComponent(Btn_Delete, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(71, 71, 71))))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(42, 42, 42)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 502, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(44, 44, 44)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(text_ara, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(Btn_Ara, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(49, 49, 49)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(Btn_Back, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(Btn_Logout1, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(text_userid, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_userid))
                        .addGap(26, 26, 26)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(text_location, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_location))
                        .addGap(29, 29, 29)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(text_age, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_age))
                        .addGap(29, 29, 29)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(text_userpassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_userpassword))
                        .addGap(61, 61, 61)
                        .addComponent(Btn_Insert, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(42, 42, 42)
                        .addComponent(Btn_Update, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(40, 40, 40)
                        .addComponent(Btn_Delete, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(69, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jFrame1Layout = new javax.swing.GroupLayout(jFrame1.getContentPane());
        jFrame1.getContentPane().setLayout(jFrame1Layout);
        jFrame1Layout.setHorizontalGroup(
            jFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jFrame1Layout.setVerticalGroup(
            jFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jPanel3.setBackground(new java.awt.Color(51, 204, 255));

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

        label_image.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        label_image.setText("Image : ");

        label_isbn.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        label_isbn.setText("ISBN : ");

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

        text_title.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        text_title.setPreferredSize(new java.awt.Dimension(69, 39));
        text_title.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                text_titleActionPerformed(evt);
            }
        });

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

        text_publisher.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        text_publisher.setPreferredSize(new java.awt.Dimension(69, 39));
        text_publisher.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                text_publisherActionPerformed(evt);
            }
        });

        label_year.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        label_year.setText("Yayin Yili : ");

        text_year.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        text_year.setPreferredSize(new java.awt.Dimension(69, 39));
        text_year.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                text_yearActionPerformed(evt);
            }
        });

        text_ara1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        text_ara1.setPreferredSize(new java.awt.Dimension(69, 39));
        text_ara1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                text_ara1ActionPerformed(evt);
            }
        });
        text_ara1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                text_ara1KeyPressed(evt);
            }
        });

        Btn_Ara1.setFont(new java.awt.Font("Tahoma", 1, 19)); // NOI18N
        Btn_Ara1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/java/icons/search.png"))); // NOI18N
        Btn_Ara1.setText(" Ara");
        Btn_Ara1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Btn_Ara1ActionPerformed(evt);
            }
        });

        label_imageurledit.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        label_imageurledit.setText("URL : ");

        text_imageurl.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        text_imageurl.setPreferredSize(new java.awt.Dimension(69, 39));
        text_imageurl.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                text_imageurlActionPerformed(evt);
            }
        });

        Btn_Back1.setFont(new java.awt.Font("Tahoma", 1, 19)); // NOI18N
        Btn_Back1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/java/icons/back.png"))); // NOI18N
        Btn_Back1.setText(" Geri Don");
        Btn_Back1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Btn_Back1ActionPerformed(evt);
            }
        });

        Btn_Logout2.setFont(new java.awt.Font("Tahoma", 1, 19)); // NOI18N
        Btn_Logout2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/java/icons/logout.png"))); // NOI18N
        Btn_Logout2.setText("Cikis Yap");
        Btn_Logout2.setIconTextGap(10);
        Btn_Logout2.setPreferredSize(new java.awt.Dimension(180, 51));
        Btn_Logout2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Btn_Logout2ActionPerformed(evt);
            }
        });

        Btn_Insert1.setFont(new java.awt.Font("Tahoma", 1, 19)); // NOI18N
        Btn_Insert1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/java/icons/insert.png"))); // NOI18N
        Btn_Insert1.setText(" Insert");
        Btn_Insert1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Btn_Insert1ActionPerformed(evt);
            }
        });

        Btn_Update1.setFont(new java.awt.Font("Tahoma", 1, 19)); // NOI18N
        Btn_Update1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/java/icons/update.png"))); // NOI18N
        Btn_Update1.setText(" Update");
        Btn_Update1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Btn_Update1ActionPerformed(evt);
            }
        });

        Btn_Delete1.setFont(new java.awt.Font("Tahoma", 1, 19)); // NOI18N
        Btn_Delete1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/java/icons/delete.png"))); // NOI18N
        Btn_Delete1.setText(" Delete");
        Btn_Delete1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Btn_Delete1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(Btn_Back1, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(96, 96, 96)
                        .addComponent(Btn_Logout2, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(146, 146, 146))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(220, 220, 220)
                                .addComponent(text_ara1, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(55, 55, 55)
                                .addComponent(Btn_Ara1, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(34, 34, 34)
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 727, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(30, 30, 30)))
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addGap(0, 60, Short.MAX_VALUE)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(Btn_Insert1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(Btn_Update1, javax.swing.GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE)
                            .addComponent(Btn_Delete1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(71, 71, 71))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(label_imageurledit)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(text_imageurl, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(jPanel3Layout.createSequentialGroup()
                                        .addComponent(label_publisher)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(text_publisher, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel3Layout.createSequentialGroup()
                                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(label_author)
                                            .addComponent(label_year)
                                            .addComponent(label_title)
                                            .addComponent(label_isbn))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                .addComponent(text_year, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(text_author, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(text_title, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addComponent(text_isbn, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                .addGap(4, 4, 4))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(label_image)
                                .addGap(18, 18, 18)
                                .addComponent(label_imageurl, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap(36, Short.MAX_VALUE))))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(42, 42, 42)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(text_isbn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_isbn))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(text_title, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_title))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(text_author, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_author))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(text_year, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_year))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(text_publisher, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_publisher))
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addComponent(label_imageurl, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(91, 91, 91)
                                .addComponent(label_image)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(text_imageurl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_imageurledit))
                        .addGap(37, 37, 37)
                        .addComponent(Btn_Insert1, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(39, 39, 39)
                        .addComponent(Btn_Update1, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 570, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(text_ara1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(Btn_Ara1, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(59, 59, 59)))
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 37, Short.MAX_VALUE)
                        .addComponent(Btn_Delete1, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(29, 29, 29))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(Btn_Logout2, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(Btn_Back1, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );

        javax.swing.GroupLayout jFrame2Layout = new javax.swing.GroupLayout(jFrame2.getContentPane());
        jFrame2.getContentPane().setLayout(jFrame2Layout);
        jFrame2Layout.setHorizontalGroup(
            jFrame2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1092, Short.MAX_VALUE)
            .addGroup(jFrame2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jFrame2Layout.setVerticalGroup(
            jFrame2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 900, Short.MAX_VALUE)
            .addGroup(jFrame2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel4.setBackground(new java.awt.Color(51, 204, 255));

        jTable3.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jTable3.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Admin ID", "Username", "Password"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable3MouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(jTable3);

        label_adminid.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        label_adminid.setText("Admin ID : ");

        text_adminid.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        text_adminid.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        text_adminid.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        text_adminid.setPreferredSize(new java.awt.Dimension(69, 39));
        text_adminid.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                text_adminidActionPerformed(evt);
            }
        });

        text_adminusername.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        text_adminusername.setPreferredSize(new java.awt.Dimension(69, 39));
        text_adminusername.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                text_adminusernameActionPerformed(evt);
            }
        });

        label_username.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        label_username.setText("Username : ");

        label_adminpassword.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        label_adminpassword.setText("Sifre : ");

        text_adminpassword.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        text_adminpassword.setPreferredSize(new java.awt.Dimension(69, 39));
        text_adminpassword.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                text_adminpasswordActionPerformed(evt);
            }
        });

        Btn_Back2.setFont(new java.awt.Font("Tahoma", 1, 19)); // NOI18N
        Btn_Back2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/java/icons/back.png"))); // NOI18N
        Btn_Back2.setText(" Geri Don");
        Btn_Back2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Btn_Back2ActionPerformed(evt);
            }
        });

        Btn_Logout3.setFont(new java.awt.Font("Tahoma", 1, 19)); // NOI18N
        Btn_Logout3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/java/icons/logout.png"))); // NOI18N
        Btn_Logout3.setText("Cikis Yap");
        Btn_Logout3.setIconTextGap(10);
        Btn_Logout3.setPreferredSize(new java.awt.Dimension(180, 51));
        Btn_Logout3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Btn_Logout3ActionPerformed(evt);
            }
        });

        Btn_Insert2.setFont(new java.awt.Font("Tahoma", 1, 19)); // NOI18N
        Btn_Insert2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/java/icons/insert.png"))); // NOI18N
        Btn_Insert2.setText(" Insert");
        Btn_Insert2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Btn_Insert2ActionPerformed(evt);
            }
        });

        Btn_Update2.setFont(new java.awt.Font("Tahoma", 1, 19)); // NOI18N
        Btn_Update2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/java/icons/update.png"))); // NOI18N
        Btn_Update2.setText(" Update");
        Btn_Update2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Btn_Update2ActionPerformed(evt);
            }
        });

        Btn_Delete2.setFont(new java.awt.Font("Tahoma", 1, 19)); // NOI18N
        Btn_Delete2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/java/icons/delete.png"))); // NOI18N
        Btn_Delete2.setText(" Delete");
        Btn_Delete2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Btn_Delete2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(177, 177, 177)
                .addComponent(Btn_Back2, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(75, 75, 75)
                .addComponent(Btn_Logout3, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(label_imageurl2, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(50, 50, 50)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 727, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(44, 44, 44)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(label_username, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(label_adminpassword, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(label_adminid, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(text_adminpassword, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(text_adminusername, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(text_adminid, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(47, Short.MAX_VALUE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(Btn_Update2, javax.swing.GroupLayout.DEFAULT_SIZE, 161, Short.MAX_VALUE)
                            .addComponent(Btn_Delete2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(Btn_Insert2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(90, 90, 90))))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(52, 52, 52)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(label_adminid)
                            .addComponent(text_adminid, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(46, 46, 46)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(text_adminusername, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_username))
                        .addGap(54, 54, 54)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(text_adminpassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_adminpassword))
                        .addGap(60, 60, 60)
                        .addComponent(Btn_Insert2, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(50, 50, 50)
                        .addComponent(Btn_Update2, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(54, 54, 54)
                        .addComponent(Btn_Delete2, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(63, 63, 63)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(Btn_Back2, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(Btn_Logout3, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(label_imageurl2, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jFrame3Layout = new javax.swing.GroupLayout(jFrame3.getContentPane());
        jFrame3.getContentPane().setLayout(jFrame3Layout);
        jFrame3Layout.setHorizontalGroup(
            jFrame3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jFrame3Layout.setVerticalGroup(
            jFrame3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(51, 204, 255));

        Btn_Books.setFont(new java.awt.Font("Tahoma", 1, 19)); // NOI18N
        Btn_Books.setIcon(new javax.swing.ImageIcon(getClass().getResource("/java/icons/books.png"))); // NOI18N
        Btn_Books.setText(" Kitaplari Duzenle");
        Btn_Books.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Btn_BooksActionPerformed(evt);
            }
        });

        Btn_Users.setFont(new java.awt.Font("Tahoma", 1, 19)); // NOI18N
        Btn_Users.setIcon(new javax.swing.ImageIcon(getClass().getResource("/java/icons/users.png"))); // NOI18N
        Btn_Users.setText(" Uyeleri Duzenle");
        Btn_Users.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Btn_UsersActionPerformed(evt);
            }
        });

        Btn_Admins.setFont(new java.awt.Font("Tahoma", 1, 19)); // NOI18N
        Btn_Admins.setIcon(new javax.swing.ImageIcon(getClass().getResource("/java/icons/adminedit.png"))); // NOI18N
        Btn_Admins.setText(" Yoneticileri Duzenle");
        Btn_Admins.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Btn_AdminsActionPerformed(evt);
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

        label_welcome.setFont(new java.awt.Font("Tahoma", 0, 21)); // NOI18N
        label_welcome.setText("Hosgeldiniz");
        label_welcome.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                label_welcomeMouseClicked(evt);
            }
        });

        text_username.setEditable(false);
        text_username.setFont(new java.awt.Font("Tahoma", 1, 15)); // NOI18N
        text_username.setPreferredSize(new java.awt.Dimension(69, 39));
        text_username.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                text_usernameActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(258, 258, 258)
                .addComponent(label_welcome)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(text_username, javax.swing.GroupLayout.DEFAULT_SIZE, 142, Short.MAX_VALUE)
                .addGap(244, 244, 244))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(Btn_Users, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(87, 87, 87)
                .addComponent(Btn_Books, javax.swing.GroupLayout.PREFERRED_SIZE, 253, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(85, 85, 85))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(279, 279, 279)
                        .addComponent(Btn_Logout, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(230, 230, 230)
                        .addComponent(Btn_Admins, javax.swing.GroupLayout.PREFERRED_SIZE, 285, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(38, 38, 38)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(text_username, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_welcome, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 57, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Btn_Books, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Btn_Users, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(42, 42, 42)
                .addComponent(Btn_Admins, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 45, Short.MAX_VALUE)
                .addComponent(Btn_Logout, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(55, 55, 55))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void Btn_LogoutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Btn_LogoutActionPerformed
        this.dispose();
        this.jFrame1.dispose();
        this.jFrame2.dispose();
        this.jFrame3.dispose();
        new AdminLoginWindow().setVisible(true);
    }//GEN-LAST:event_Btn_LogoutActionPerformed

    private void label_welcomeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_label_welcomeMouseClicked

    }//GEN-LAST:event_label_welcomeMouseClicked

    private void text_usernameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_text_usernameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_text_usernameActionPerformed

    private void Btn_AraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Btn_AraActionPerformed
        try {
            filteredUserstablodaGoster();
        } catch (SQLException | ClassNotFoundException ex) {
            Logger.getLogger(AdminWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_Btn_AraActionPerformed

    private void text_araKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_text_araKeyPressed
        int key = evt.getKeyCode();
        if (key == KeyEvent.VK_ENTER) {
            clicked1 = true;
            try {
                filteredUserstablodaGoster();
            } catch (SQLException | ClassNotFoundException ex) {
                Logger.getLogger(AdminWindow.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_text_araKeyPressed

    private void text_araActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_text_araActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_text_araActionPerformed

    private void text_userpasswordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_text_userpasswordActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_text_userpasswordActionPerformed

    private void text_ageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_text_ageActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_text_ageActionPerformed

    private void text_locationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_text_locationActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_text_locationActionPerformed

    private void text_useridActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_text_useridActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_text_useridActionPerformed

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        int index = jTable1.getSelectedRow();
        Btn_Ara.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == Btn_Ara) {
                    clicked1 = true;
                }
            }
        });
        if (clicked1 == false) {
            try {
                showItem1(index);
            } catch (SQLException | ClassNotFoundException | IOException ex) {
                Logger.getLogger(AdminWindow.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            try {
                showItem4(index);
            } catch (SQLException | ClassNotFoundException | IOException ex) {
                Logger.getLogger(AdminWindow.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_jTable1MouseClicked

    private void Btn_BackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Btn_BackActionPerformed
        jTable1.getSelectionModel().clearSelection();
        this.jFrame1.setVisible(false);
        this.setVisible(true);
    }//GEN-LAST:event_Btn_BackActionPerformed

    private void Btn_Logout1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Btn_Logout1ActionPerformed
        this.dispose();
        this.jFrame1.dispose();
        this.jFrame2.dispose();
        this.jFrame3.dispose();
        new AdminLoginWindow().setVisible(true);
    }//GEN-LAST:event_Btn_Logout1ActionPerformed

    private void jTable2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable2MouseClicked
        int index = jTable2.getSelectedRow();
        Btn_Ara1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == Btn_Ara1) {
                    clicked2 = true;
                }
            }
        });
        if (clicked2 == false) {
            try {
                showItem2(index);
            } catch (SQLException | ClassNotFoundException | IOException ex) {
                Logger.getLogger(AdminWindow.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            try {
                showItem5(index);
            } catch (SQLException | ClassNotFoundException | IOException ex) {
                Logger.getLogger(AdminWindow.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_jTable2MouseClicked

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

    private void text_yearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_text_yearActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_text_yearActionPerformed

    private void text_ara1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_text_ara1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_text_ara1ActionPerformed

    private void text_ara1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_text_ara1KeyPressed
        int key = evt.getKeyCode();
        if (key == KeyEvent.VK_ENTER) {
            clicked2 = true;
            try {
                filteredBookstablodaGoster();
            } catch (SQLException | ClassNotFoundException ex) {
                Logger.getLogger(AdminWindow.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_text_ara1KeyPressed

    private void Btn_Ara1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Btn_Ara1ActionPerformed
        try {
            filteredBookstablodaGoster();
        } catch (SQLException | ClassNotFoundException ex) {
            Logger.getLogger(AdminWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_Btn_Ara1ActionPerformed

    private void text_imageurlActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_text_imageurlActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_text_imageurlActionPerformed

    private void Btn_Back1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Btn_Back1ActionPerformed
        jTable2.getSelectionModel().clearSelection();
        this.jFrame2.setVisible(false);
        this.jFrame2.setEnabled(false);
        this.setVisible(true);
    }//GEN-LAST:event_Btn_Back1ActionPerformed

    private void Btn_Logout2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Btn_Logout2ActionPerformed
        this.dispose();
        this.jFrame1.dispose();
        this.jFrame2.dispose();
        this.jFrame3.dispose();
        new AdminLoginWindow().setVisible(true);
    }//GEN-LAST:event_Btn_Logout2ActionPerformed

    private void Btn_UsersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Btn_UsersActionPerformed
        this.setVisible(false);
        this.jFrame1.setEnabled(true);
        this.jFrame1.pack();
        this.jFrame1.setLocationRelativeTo(null);
        this.jFrame1.setVisible(true);
        try {
            tablodaGoster1();
        } catch (SQLException | ClassNotFoundException ex) {
            Logger.getLogger(AdminWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_Btn_UsersActionPerformed

    private void Btn_BooksActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Btn_BooksActionPerformed
        this.setVisible(false);
        this.jFrame2.setEnabled(true);
        this.jFrame2.pack();
        this.jFrame2.setLocationRelativeTo(null);
        this.jFrame2.setVisible(true);
        try {
            tablodaGoster2();
        } catch (SQLException | ClassNotFoundException ex) {
            Logger.getLogger(AdminWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_Btn_BooksActionPerformed

    private void Btn_AdminsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Btn_AdminsActionPerformed
        this.setVisible(false);
        this.jFrame3.setEnabled(true);
        this.jFrame3.pack();
        this.jFrame3.setLocationRelativeTo(null);
        this.jFrame3.setVisible(true);
        try {
            tablodaGoster3();
        } catch (SQLException | ClassNotFoundException ex) {
            Logger.getLogger(AdminWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_Btn_AdminsActionPerformed

    private void Btn_Logout3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Btn_Logout3ActionPerformed
        this.dispose();
        this.jFrame1.dispose();
        this.jFrame2.dispose();
        this.jFrame3.dispose();
        new AdminLoginWindow().setVisible(true);
    }//GEN-LAST:event_Btn_Logout3ActionPerformed

    private void Btn_Back2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Btn_Back2ActionPerformed
        jTable3.getSelectionModel().clearSelection();
        this.jFrame3.setVisible(false);
        this.jFrame3.setEnabled(false);
        this.setVisible(true);
    }//GEN-LAST:event_Btn_Back2ActionPerformed

    private void text_adminpasswordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_text_adminpasswordActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_text_adminpasswordActionPerformed

    private void text_adminusernameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_text_adminusernameActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_text_adminusernameActionPerformed

    private void text_adminidActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_text_adminidActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_text_adminidActionPerformed

    private void jTable3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable3MouseClicked
        int index = jTable3.getSelectedRow();
        try {
            showItem3(index);
        } catch (SQLException | ClassNotFoundException | IOException ex) {
            Logger.getLogger(AdminWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jTable3MouseClicked

    private void Btn_InsertActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Btn_InsertActionPerformed
        if (checkInputs1()) {
            try {
                if (checkID(text_userid.getText())) {
                    JOptionPane.showMessageDialog(null, "<html><b>Bu ID numarasi kullanilmaktadir.</b></html>");
                } else {
                    Connection conn = null;
                    try {
                        conn = getConnection();
                    } catch (SQLException | ClassNotFoundException ex) {
                        Logger.getLogger(AdminWindow.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    PreparedStatement ps;
                    try {
                        ps = conn.prepareStatement("INSERT INTO `BX-Users`(`User-ID`, Location, Age, Password) VALUES(?, ?, ?, ?);");
                        ps.setString(1, text_userid.getText());
                        ps.setString(2, text_location.getText());
                        ps.setString(3, text_age.getText());
                        ps.setString(4, text_userpassword.getText());
                        ps.executeUpdate();
                        tablodaGoster1();
                        JOptionPane.showMessageDialog(null, "<html><b>Uye eklendi.</b></html>");
                    } catch (SQLException ex) {
                        Logger.getLogger(AdminWindow.class.getName()).log(Level.SEVERE, null, ex);
                        JOptionPane.showMessageDialog(null, "<html><b>Kayit ekleme sirasinda hata olustu.</b></html>");
                    } catch (ClassNotFoundException ex) {
                        Logger.getLogger(AdminWindow.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            } catch (SQLException | ClassNotFoundException ex) {
                Logger.getLogger(AdminWindow.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            JOptionPane.showMessageDialog(null, "<html><b>Bir ya da daha fazla alani bos biraktiniz.</b></html>");
        }
    }//GEN-LAST:event_Btn_InsertActionPerformed

    private void Btn_Insert1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Btn_Insert1ActionPerformed
        if (checkInputs2()) {
            try {
                if (checkISBN(text_isbn.getText())) {
                    JOptionPane.showMessageDialog(null, "<html><b>Bu kitap sistemde mevcuttur.</b></html>");
                } else {
                    Connection conn = null;
                    try {
                        conn = getConnection();
                    } catch (SQLException | ClassNotFoundException ex) {
                        Logger.getLogger(AdminWindow.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    PreparedStatement ps;
                    try {
                        ps = conn.prepareStatement("INSERT INTO `BX-Books`(ISBN, `Book-Title`, `Book-Author`, `Year-Of-Publication`, Publisher, `Image-URL-M`) VALUES(?, ?, ?, ?, ?, ?);");
                        ps.setString(1, text_isbn.getText());
                        ps.setString(2, text_title.getText());
                        ps.setString(3, text_author.getText());
                        ps.setString(4, text_year.getText());
                        ps.setString(5, text_publisher.getText());
                        ps.setString(6, text_imageurl.getText());
                        ps.executeUpdate();
                        tablodaGoster2();
                        JOptionPane.showMessageDialog(null, "<html><b>Kitap eklendi.</b></html>");
                    } catch (SQLException ex) {
                        Logger.getLogger(AdminWindow.class.getName()).log(Level.SEVERE, null, ex);
                        JOptionPane.showMessageDialog(null, "<html><b>Kayit ekleme sirasinda hata olustu.</b></html>");
                    } catch (ClassNotFoundException ex) {
                        Logger.getLogger(AdminWindow.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            } catch (SQLException | ClassNotFoundException ex) {
                Logger.getLogger(AdminWindow.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            JOptionPane.showMessageDialog(null, "<html><b>Bir ya da daha fazla alani bos biraktiniz.</b></html>");
        }
    }//GEN-LAST:event_Btn_Insert1ActionPerformed

    private void Btn_Insert2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Btn_Insert2ActionPerformed
        if (checkInputs3()) {
            try {
                if (checkAdminID(text_userid.getText())) {
                    JOptionPane.showMessageDialog(null, "<html><b>Bu ID numarasi kullanilmaktadir.</b></html>");
                } else {
                    if (checkAdminUsername(text_adminusername.getText())) {
                        JOptionPane.showMessageDialog(null, "<html><b>Bu kullanici adi kullanilmaktadir.</b></html>");
                    } else {
                        Connection conn = null;
                        try {
                            conn = getConnection();
                        } catch (SQLException | ClassNotFoundException ex) {
                            Logger.getLogger(AdminWindow.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        PreparedStatement ps;
                        try {
                            ps = conn.prepareStatement("INSERT INTO `BX-Admin`(ID, Username, Password) VALUES(?, ?, ?);");
                            ps.setString(1, text_adminid.getText());
                            ps.setString(2, text_adminusername.getText());
                            ps.setString(3, text_adminpassword.getText());
                            ps.executeUpdate();
                            tablodaGoster3();
                            JOptionPane.showMessageDialog(null, "<html><b>Yonetici eklendi.</b></html>");
                        } catch (SQLException ex) {
                            Logger.getLogger(AdminWindow.class.getName()).log(Level.SEVERE, null, ex);
                            JOptionPane.showMessageDialog(null, "<html><b>Kayit ekleme sirasinda hata olustu.</b></html>");
                        } catch (ClassNotFoundException ex) {
                            Logger.getLogger(AdminWindow.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            } catch (SQLException | ClassNotFoundException ex) {
                Logger.getLogger(AdminWindow.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            JOptionPane.showMessageDialog(null, "<html><b>Bir ya da daha fazla alani bos biraktiniz.</b></html>");
        }
    }//GEN-LAST:event_Btn_Insert2ActionPerformed

    private void Btn_UpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Btn_UpdateActionPerformed
        if (checkInputs1()) {
            String query = "UPDATE `BX-Users` SET Location = ?, Age = ?, Password = ? WHERE `User-ID` = ?;";
            Connection conn = null;
            try {
                conn = getConnection();
            } catch (SQLException | ClassNotFoundException ex) {
                Logger.getLogger(AdminWindow.class.getName()).log(Level.SEVERE, null, ex);
            }
            PreparedStatement ps;
            try {
                ps = conn.prepareStatement(query);
                ps.setString(1, text_location.getText());
                ps.setString(2, text_age.getText());
                ps.setString(3, text_userpassword.getText());
                ps.setString(4, text_userid.getText());
                ps.executeUpdate();
                tablodaGoster1();
                JOptionPane.showMessageDialog(null, "<html><b>Uye guncellendi.</b></html>");
            } catch (SQLException ex) {
                Logger.getLogger(AdminWindow.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(null, "<html><b>Kayit guncelleme sirasinda hata olustu.</b></html>");
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(AdminWindow.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            JOptionPane.showMessageDialog(null, "<html><b>Bir ya da daha fazla alani bos biraktiniz.</b></html>");
        }
    }//GEN-LAST:event_Btn_UpdateActionPerformed

    private void Btn_Update1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Btn_Update1ActionPerformed
        if (checkInputs2()) {
            String query = "UPDATE `BX-Books` SET `Book-Title` = ?, `Book-Author` = ?, `Year-Of-Publication` = ?, Publisher = ?, `Image-URL-M` = ? WHERE ISBN = ?;";
            Connection conn = null;
            try {
                conn = getConnection();
            } catch (SQLException | ClassNotFoundException ex) {
                Logger.getLogger(AdminWindow.class.getName()).log(Level.SEVERE, null, ex);
            }
            PreparedStatement ps;
            try {
                ps = conn.prepareStatement(query);
                ps.setString(1, text_title.getText());
                ps.setString(2, text_author.getText());
                ps.setString(3, text_year.getText());
                ps.setString(4, text_publisher.getText());
                ps.setString(5, text_imageurl.getText());
                ps.setString(6, text_isbn.getText());
                ps.executeUpdate();
                tablodaGoster2();
                JOptionPane.showMessageDialog(null, "<html><b>Kitap guncellendi.</b></html>");
            } catch (SQLException ex) {
                Logger.getLogger(AdminWindow.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(null, "<html><b>Kayit guncelleme sirasinda hata olustu.</b></html>");
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(AdminWindow.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            JOptionPane.showMessageDialog(null, "<html><b>Bir ya da daha fazla alani bos biraktiniz.</b></html>");
        }
    }//GEN-LAST:event_Btn_Update1ActionPerformed

    private void Btn_Update2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Btn_Update2ActionPerformed
        if (checkInputs1()) {
            String query = "UPDATE `BX-Admin` SET Username = ?, Password = ? WHERE ID = ?;";
            Connection conn = null;
            try {
                conn = getConnection();
            } catch (SQLException | ClassNotFoundException ex) {
                Logger.getLogger(AdminWindow.class.getName()).log(Level.SEVERE, null, ex);
            }
            PreparedStatement ps;
            try {
                ps = conn.prepareStatement(query);
                ps.setString(1, text_adminusername.getText());
                ps.setString(2, text_adminpassword.getText());
                ps.setString(3, text_adminid.getText());
                ps.executeUpdate();
                tablodaGoster3();
                JOptionPane.showMessageDialog(null, "<html><b>Yonetici guncellendi.</b></html>");
            } catch (SQLException ex) {
                Logger.getLogger(AdminWindow.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(null, "<html><b>Kayit guncelleme sirasinda hata olustu.</b></html>");
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(AdminWindow.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            JOptionPane.showMessageDialog(null, "<html><b>Bir ya da daha fazla alani bos biraktiniz.</b></html>");
        }
    }//GEN-LAST:event_Btn_Update2ActionPerformed

    private void Btn_Delete1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Btn_Delete1ActionPerformed
        if (!text_isbn.getText().equals("")) {
            Connection conn = null;
            try {
                conn = getConnection();
            } catch (SQLException | ClassNotFoundException ex) {
                Logger.getLogger(AdminWindow.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                PreparedStatement ps = conn.prepareStatement("DELETE FROM `BX-Books` WHERE ISBN = ?;");
                ps.setString(1, text_isbn.getText());
                ps.executeUpdate();
                tablodaGoster2();
                JOptionPane.showMessageDialog(null, "<html><b>Kitap silindi.</b></html>");
            } catch (SQLException ex) {
                Logger.getLogger(AdminWindow.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(null, "<html><b>Kayit silme sirasinda hata olustu.</b></html>");
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(AdminWindow.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            JOptionPane.showMessageDialog(null, "<html><b>Kitap silinemedi. Dogru ISBN numarasini girdiginizden emin olun.</b></html>");
        }
    }//GEN-LAST:event_Btn_Delete1ActionPerformed

    private void Btn_DeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Btn_DeleteActionPerformed
        if (!text_userid.getText().equals("")) {
            Connection conn = null;
            try {
                conn = getConnection();
            } catch (SQLException | ClassNotFoundException ex) {
                Logger.getLogger(AdminWindow.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                PreparedStatement ps = conn.prepareStatement("DELETE FROM `BX-Users` WHERE `User-ID` = ?;");
                int id = Integer.parseInt(text_userid.getText());
                ps.setInt(1, id);
                ps.executeUpdate();
                tablodaGoster1();
                JOptionPane.showMessageDialog(null, "<html><b>Uye silindi.</b></html>");
            } catch (SQLException ex) {
                Logger.getLogger(AdminWindow.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(null, "<html><b>Kayit silme sirasinda hata olustu.</b></html>");
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(AdminWindow.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            JOptionPane.showMessageDialog(null, "<html><b>Uye silinemedi. Dogru ID numarasini girdiginizden emin olun.</b></html>");
        }
    }//GEN-LAST:event_Btn_DeleteActionPerformed

    private void Btn_Delete2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Btn_Delete2ActionPerformed
        if (!text_userid.getText().equals("")) {
            Connection conn = null;
            try {
                conn = getConnection();
            } catch (SQLException | ClassNotFoundException ex) {
                Logger.getLogger(AdminWindow.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                PreparedStatement ps = conn.prepareStatement("DELETE FROM `BX-Admin` WHERE ID = ?;");
                int id = Integer.parseInt(text_adminid.getText());
                ps.setInt(1, id);
                ps.executeUpdate();
                tablodaGoster3();
                JOptionPane.showMessageDialog(null, "<html><b>Yonetici silindi.</b></html>");
            } catch (SQLException ex) {
                Logger.getLogger(AdminWindow.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(null, "<html><b>Kayit silme sirasinda hata olustu.</b></html>");
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(AdminWindow.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            JOptionPane.showMessageDialog(null, "<html><b>Yonetici silinemedi. Dogru ID numarasini girdiginizden emin olun.</b></html>");
        }
    }//GEN-LAST:event_Btn_Delete2ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Btn_Admins;
    private javax.swing.JButton Btn_Ara;
    private javax.swing.JButton Btn_Ara1;
    private javax.swing.JButton Btn_Back;
    private javax.swing.JButton Btn_Back1;
    private javax.swing.JButton Btn_Back2;
    private javax.swing.JButton Btn_Books;
    private javax.swing.JButton Btn_Delete;
    private javax.swing.JButton Btn_Delete1;
    private javax.swing.JButton Btn_Delete2;
    private javax.swing.JButton Btn_Insert;
    private javax.swing.JButton Btn_Insert1;
    private javax.swing.JButton Btn_Insert2;
    private javax.swing.JButton Btn_Logout;
    private javax.swing.JButton Btn_Logout1;
    private javax.swing.JButton Btn_Logout2;
    private javax.swing.JButton Btn_Logout3;
    private javax.swing.JButton Btn_Update;
    private javax.swing.JButton Btn_Update1;
    private javax.swing.JButton Btn_Update2;
    private javax.swing.JButton Btn_Users;
    private javax.swing.JFrame jFrame1;
    private javax.swing.JFrame jFrame2;
    private javax.swing.JFrame jFrame3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    private javax.swing.JTable jTable3;
    private javax.swing.JLabel label_adminid;
    private javax.swing.JLabel label_adminpassword;
    private javax.swing.JLabel label_age;
    private javax.swing.JLabel label_author;
    private javax.swing.JLabel label_image;
    private javax.swing.JLabel label_imageurl;
    private javax.swing.JLabel label_imageurl2;
    private javax.swing.JLabel label_imageurledit;
    private javax.swing.JLabel label_isbn;
    private javax.swing.JLabel label_location;
    private javax.swing.JLabel label_publisher;
    private javax.swing.JLabel label_title;
    private javax.swing.JLabel label_userid;
    private javax.swing.JLabel label_username;
    private javax.swing.JLabel label_userpassword;
    private javax.swing.JLabel label_welcome;
    private javax.swing.JLabel label_year;
    private javax.swing.JTextField text_adminid;
    private javax.swing.JTextField text_adminpassword;
    private javax.swing.JTextField text_adminusername;
    private javax.swing.JTextField text_age;
    private javax.swing.JTextField text_ara;
    private javax.swing.JTextField text_ara1;
    private javax.swing.JTextField text_author;
    private javax.swing.JTextField text_imageurl;
    private javax.swing.JTextField text_isbn;
    private javax.swing.JTextField text_location;
    private javax.swing.JTextField text_publisher;
    private javax.swing.JTextField text_title;
    private javax.swing.JTextField text_userid;
    private javax.swing.JTextField text_username;
    private javax.swing.JTextField text_userpassword;
    private javax.swing.JTextField text_year;
    // End of variables declaration//GEN-END:variables
}
