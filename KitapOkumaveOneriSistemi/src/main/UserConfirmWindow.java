
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
public class UserConfirmWindow extends javax.swing.JFrame {
    
    private final String kayitid;
    private final String konum;
    private final String yas;
    private final String password;
    private final ArrayList<String> isbnlist = new ArrayList<String>();
    private final ArrayList<Object> ratinglist = new ArrayList<Object>();
    
    private boolean clicked = false;
    
    /**
     * Creates new form UserConfirmWindow
     * @param kayitid
     * @param konum
     * @param yas
     * @param password
     * @throws java.sql.SQLException
     * @throws java.lang.ClassNotFoundException
     */
    public UserConfirmWindow(String kayitid, String konum, String yas, String password) throws SQLException, ClassNotFoundException {
        initComponents();
        this.setLocationRelativeTo(null);
        this.setTitle("Kitap Okuma ve Oneri Sistemi");
        this.kayitid = kayitid;
        this.konum = konum;
        this.yas = yas;
        this.password = password;
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
            Logger.getLogger(UserConfirmWindow.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, "<html><b>Veritabanina baglanti saglanamadi</b></html>");
        }
        return null;
    }
    
    public final void showValues() throws SQLException, ClassNotFoundException {
        text_uyeid.setText(kayitid);
        text_uyeid.setBackground(Color.YELLOW);
        text_uyeid.setEditable(false);
        text_kalankitap.setText("10");
        text_kalankitap.setBackground(Color.YELLOW);
        text_kalankitap.setEditable(false);
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
            Logger.getLogger(UserConfirmWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
        return booklist;
    }
    
    public final void tablodaGoster() throws SQLException, ClassNotFoundException {
        ArrayList<Books> list = getBookList();
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
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
            Logger.getLogger(UserConfirmWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void showItem2(int index) throws SQLException, ClassNotFoundException, MalformedURLException, IOException {
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
            Logger.getLogger(UserConfirmWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public boolean checkInputs() {
        if (text_isbn.getText().trim().isEmpty()) {
            return false;
        } else {
            return true;
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
            Logger.getLogger(UserConfirmWindow.class.getName()).log(Level.SEVERE, null, ex);
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

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        label_bilgi = new javax.swing.JLabel();
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
        Btn_Kayit = new javax.swing.JButton();
        Btn_Menu = new javax.swing.JButton();
        label_year = new javax.swing.JLabel();
        text_year = new javax.swing.JTextField();
        Btn_Oy = new javax.swing.JButton();
        text_kalankitap = new javax.swing.JTextField();
        label_kalankitap = new javax.swing.JLabel();
        text_ara = new javax.swing.JTextField();
        Btn_Ara = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(51, 204, 255));

        label_bilgi.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        label_bilgi.setText("Uyeliginizin tamamlanmasi icin en az 10 kitap oylamaniz gerekmektedir.");
        label_bilgi.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                label_bilgiMouseClicked(evt);
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
        ));
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

        Btn_Kayit.setFont(new java.awt.Font("Tahoma", 1, 19)); // NOI18N
        Btn_Kayit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/java/icons/confirm.png"))); // NOI18N
        Btn_Kayit.setText(" Uyeligimi Tamamla");
        Btn_Kayit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Btn_KayitActionPerformed(evt);
            }
        });

        Btn_Menu.setFont(new java.awt.Font("Tahoma", 1, 19)); // NOI18N
        Btn_Menu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/java/icons/home.png"))); // NOI18N
        Btn_Menu.setText("Ana Ekrana Git");
        Btn_Menu.setIconTextGap(10);
        Btn_Menu.setPreferredSize(new java.awt.Dimension(180, 51));
        Btn_Menu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Btn_MenuActionPerformed(evt);
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

        text_kalankitap.setBackground(new java.awt.Color(51, 204, 255));
        text_kalankitap.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N

        label_kalankitap.setFont(new java.awt.Font("Tahoma", 0, 20)); // NOI18N
        label_kalankitap.setText("kitap daha kaldi.");

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

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(274, 274, 274)
                        .addComponent(label_id)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(text_uyeid, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(82, 82, 82)
                        .addComponent(label_bilgi)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(38, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 727, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(26, 26, 26))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(79, 79, 79)
                                .addComponent(Btn_Kayit, javax.swing.GroupLayout.PREFERRED_SIZE, 267, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(85, 85, 85)
                                .addComponent(Btn_Menu, javax.swing.GroupLayout.PREFERRED_SIZE, 225, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(174, 174, 174)
                                .addComponent(text_ara, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(54, 54, 54)
                                .addComponent(Btn_Ara, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addGap(27, 27, 27)
                            .addComponent(label_image)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(label_imageurl, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addComponent(label_publisher)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(text_publisher, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(text_kalankitap, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(label_kalankitap))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(label_author)
                                            .addComponent(label_year)
                                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addComponent(label_isbn)
                                                .addComponent(label_title)))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(text_year, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(text_author, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(text_title, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(text_isbn, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                            .addGap(4, 4, 4)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(label_rating)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(combobox_rating, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(76, 76, 76))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(Btn_Oy, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(85, 85, 85)))))
                .addGap(26, 26, 26))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(text_uyeid, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(label_id))
                .addGap(6, 6, 6)
                .addComponent(label_bilgi, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 16, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(label_isbn)
                            .addComponent(text_isbn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(20, 20, 20)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(label_title)
                            .addComponent(text_title, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(label_author)
                            .addComponent(text_author, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addComponent(text_year, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, Short.MAX_VALUE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(label_year)
                                .addGap(33, 33, 33)))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(text_publisher, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_publisher))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(99, 99, 99)
                                .addComponent(label_image))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addComponent(label_imageurl, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(15, 15, 15)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(label_rating)
                            .addComponent(combobox_rating, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(4, 4, 4)
                        .addComponent(Btn_Oy, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(text_kalankitap, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(label_kalankitap)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 491, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(21, 21, 21)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(text_ara, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(Btn_Ara, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(40, 40, 40)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(Btn_Kayit, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(Btn_Menu, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(64, 64, 64))
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

    private void label_bilgiMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_label_bilgiMouseClicked

    }//GEN-LAST:event_label_bilgiMouseClicked

    private void combobox_ratingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_combobox_ratingActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_combobox_ratingActionPerformed

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
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
                Logger.getLogger(UserConfirmWindow.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            try {
                showItem2(index);
            } catch (SQLException | ClassNotFoundException | IOException ex) {
                Logger.getLogger(UserConfirmWindow.class.getName()).log(Level.SEVERE, null, ex);
            }
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

    private void Btn_KayitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Btn_KayitActionPerformed
        if (Integer.parseInt(text_kalankitap.getText()) == 0) {
            Connection conn = null;
            try {
                conn = getConnection();
            } catch (SQLException | ClassNotFoundException ex) {
                Logger.getLogger(UserConfirmWindow.class.getName()).log(Level.SEVERE, null, ex);
            }
            PreparedStatement ps;
            try {
                ps = conn.prepareStatement("INSERT INTO `BX-Users`(`User-ID`, Location, Age, Password) VALUES(?, ?, ?, ?);");
                ps.setString(1, kayitid);
                ps.setString(2, konum);
                ps.setString(3, yas);
                ps.setString(4, password);
                ps.executeUpdate();

                for (int i = 0; i < isbnlist.size(); i++) {
                    ps = conn.prepareStatement("INSERT INTO `BX-Book-Ratings`(`User-ID`, ISBN, `Book-Rating`) VALUES(?, ?, ?);");
                    ps.setString(1, kayitid);
                    ps.setString(2, isbnlist.get(i));
                    ps.setString(3, ratinglist.get(i).toString());
                    ps.executeUpdate();
                }
            } catch (SQLException ex) {
                Logger.getLogger(UserConfirmWindow.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(null, "<html><b>Kayit ekleme sirasinda hata olustu.</b></html>");
            }
            JOptionPane.showMessageDialog(null, "<html><b>Uyeliginiz tamamlanmistir. Ana menuye giderek giris yapabilirsiniz.</b></html>");
        } else if (Integer.parseInt(text_kalankitap.getText()) == 10) {
            JOptionPane.showMessageDialog(null, "<html><b>Uyeliginizin tamamlanmasi icin en az 10 kitap oylamaniz gerekmektedir.</b></html>");
        } else if (Integer.parseInt(text_kalankitap.getText()) == 9) {
            JOptionPane.showMessageDialog(null, "<html><b>Uyeliginizin tamamlanmasi icin en az 9 kitap daha oylamaniz gerekmektedir.</b></html>");
        } else if (Integer.parseInt(text_kalankitap.getText()) == 8) {
            JOptionPane.showMessageDialog(null, "<html><b>Uyeliginizin tamamlanmasi icin en az 8 kitap daha oylamaniz gerekmektedir.</b></html>");
        } else if (Integer.parseInt(text_kalankitap.getText()) == 7) {
            JOptionPane.showMessageDialog(null, "<html><b>Uyeliginizin tamamlanmasi icin en az 7 kitap daha oylamaniz gerekmektedir.</b></html>");
        } else if (Integer.parseInt(text_kalankitap.getText()) == 6) {
            JOptionPane.showMessageDialog(null, "<html><b>Uyeliginizin tamamlanmasi icin en az 6 kitap daha oylamaniz gerekmektedir.</b></html>");
        } else if (Integer.parseInt(text_kalankitap.getText()) == 5) {
            JOptionPane.showMessageDialog(null, "<html><b>Uyeliginizin tamamlanmasi icin en az 5 kitap daha oylamaniz gerekmektedir.</b></html>");
        } else if (Integer.parseInt(text_kalankitap.getText()) == 4) {
            JOptionPane.showMessageDialog(null, "<html><b>Uyeliginizin tamamlanmasi icin en az 4 kitap daha oylamaniz gerekmektedir.</b></html>");
        } else if (Integer.parseInt(text_kalankitap.getText()) == 3) {
            JOptionPane.showMessageDialog(null, "<html><b>Uyeliginizin tamamlanmasi icin en az 3 kitap daha oylamaniz gerekmektedir.</b></html>");
        } else if (Integer.parseInt(text_kalankitap.getText()) == 2) {
            JOptionPane.showMessageDialog(null, "<html><b>Uyeliginizin tamamlanmasi icin en az 2 kitap daha oylamaniz gerekmektedir.</b></html>");
        } else if (Integer.parseInt(text_kalankitap.getText()) == 1) {
            JOptionPane.showMessageDialog(null, "<html><b>Uyeliginizin tamamlanmasi icin en az 1 kitap daha oylamaniz gerekmektedir.</b></html>");
        } else {
            JOptionPane.showMessageDialog(null, "<html><b>Uyeliginizin tamamlanmasi sirasinda hata olusmustur.</b></html>");
        }
    }//GEN-LAST:event_Btn_KayitActionPerformed

    private void Btn_MenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Btn_MenuActionPerformed
        this.dispose();
        new MainWindow().setVisible(true);
    }//GEN-LAST:event_Btn_MenuActionPerformed

    private void text_yearActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_text_yearActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_text_yearActionPerformed

    private void Btn_OyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Btn_OyActionPerformed
        if (checkInputs()) {
            if (isbnlist.contains(text_isbn.getText())) {
                JOptionPane.showMessageDialog(null, "<html><b>Bu kitabi zaten oyladiniz.</b></html>");
            } else {
                isbnlist.add(text_isbn.getText());
                ratinglist.add(combobox_rating.getSelectedItem().toString());
                if (Integer.parseInt(text_kalankitap.getText()) > 0) {
                    text_kalankitap.setText(Integer.toString(Integer.parseInt(text_kalankitap.getText()) - 1));
                }
                JOptionPane.showMessageDialog(null, "<html><b>Oyunuz basariyla kaydedilmistir.</b></html>");
            }
        } else {
            JOptionPane.showMessageDialog(null, "<html><b>Oy vermek icin bir kitap seciniz.</b></html>");
        }
    }//GEN-LAST:event_Btn_OyActionPerformed

    private void text_araActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_text_araActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_text_araActionPerformed

    private void Btn_AraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Btn_AraActionPerformed
        try {
            filteredtablodaGoster();
        } catch (SQLException | ClassNotFoundException ex) {
            Logger.getLogger(UserConfirmWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_Btn_AraActionPerformed

    private void text_araKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_text_araKeyPressed
        int key = evt.getKeyCode();
        if (key == KeyEvent.VK_ENTER) {
            clicked = true;
            try {
                filteredtablodaGoster();
            } catch (SQLException | ClassNotFoundException ex) {
                Logger.getLogger(UserConfirmWindow.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_text_araKeyPressed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Btn_Ara;
    private javax.swing.JButton Btn_Kayit;
    private javax.swing.JButton Btn_Menu;
    private javax.swing.JButton Btn_Oy;
    private javax.swing.JComboBox<String> combobox_rating;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JLabel label_author;
    private javax.swing.JLabel label_bilgi;
    private javax.swing.JLabel label_id;
    private javax.swing.JLabel label_image;
    private javax.swing.JLabel label_imageurl;
    private javax.swing.JLabel label_isbn;
    private javax.swing.JLabel label_kalankitap;
    private javax.swing.JLabel label_publisher;
    private javax.swing.JLabel label_rating;
    private javax.swing.JLabel label_title;
    private javax.swing.JLabel label_year;
    private javax.swing.JTextField text_ara;
    private javax.swing.JTextField text_author;
    private javax.swing.JTextField text_isbn;
    private javax.swing.JTextField text_kalankitap;
    private javax.swing.JTextField text_publisher;
    private javax.swing.JTextField text_title;
    private javax.swing.JTextField text_uyeid;
    private javax.swing.JTextField text_year;
    // End of variables declaration//GEN-END:variables
}
