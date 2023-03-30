import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.*;
import java.sql.*;

public class TennisApp {
    private JTabbedPane tabbedPane1;
    private JPanel panel1;
    private JTextField txtSearch;
    private JButton btnUpdatePlayers;
    private JButton btnDeletePlayer;
    private JButton btnAddPlayer;
    private JTextField txtLastName;
    private JTextField txtFirstName;
    private JTextField txtGender;
    private JButton btnUpdatePlayer;
    private JTextField txtId;
    private JButton afficherListeButton;
    private JButton searchAllButton;
    private JTable tblData;
    private JCheckBox hommeCheckBox;
    private JCheckBox femmeCheckBox;
    private JTextField txtNomTournois;
    private JTextField txtCodeTournois;
    private JButton supprimerButtonTournois;
    private JButton editerButtonTournois;
    private JButton ajouterButtonTournois;
    private JTable tableDataTournois;
    private JButton afficherTournois;
    private JTextField txtIdTournois;
    private JTextField txtSearchTournois;
    private JTextField searchMatchs;
    private JTable tableMatchs;
    private JButton afficherMatchs;
    private JButton afficherFinalistes;
    private JButton afficherScores;
    private JTable tableScores;
    private JTextField searchVicDef;
    private JTable tableVicDef;
    private JTextField searchEpreuves;
    private JTable tableEpreuves;

    String id, lastName, firstName, gender, nomTournois, codeTournois, idTournois, idMatchs, nomMatchs, prenomMatchs, sexeMatchs;
    Connection con;
    PreparedStatement pst;

    public void Connect() {
        try {
            Class.forName("org.mariadb.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mariadb://localhost/tennis", "root", "");
            System.out.println("Success");
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void afficherDataBase(PreparedStatement pst) throws SQLException {
        ResultSet rs = pst.executeQuery();
        ResultSetMetaData rsmd = rs.getMetaData();
        DefaultTableModel model = (DefaultTableModel) tblData.getModel();
        int cols = rsmd.getColumnCount();
        String[] colName = new String[cols];
        for (int i = 0; i < cols; i++) {
            colName[i] = rsmd.getColumnName(i + 1);
            model.setColumnIdentifiers(colName);

            String idData, lastNameData, firstNameData, sexeData;

            //read row one by one & add in the table
            while (rs.next()) {
                idData = rs.getString(1);
                lastNameData = rs.getString(2);
                firstNameData = rs.getString(3);
                sexeData = rs.getString(4);
                String[] row = {idData, lastNameData, firstNameData, sexeData};
                model.addRow(row);
            }
        }
    }

    public void afficherDataBaseTournois(PreparedStatement pst) throws SQLException {
        ResultSet rs = pst.executeQuery();
        ResultSetMetaData rsmd = rs.getMetaData();
        DefaultTableModel model = (DefaultTableModel) tableDataTournois.getModel();
        int cols = rsmd.getColumnCount();
        String[] colName = new String[cols];
        for (int i = 0; i < cols; i++) {
            colName[i] = rsmd.getColumnName(i + 1);
            model.setColumnIdentifiers(colName);

            String idData, nameDataTournois, codeDataTournois;

            while (rs.next()) {
                idData = rs.getString(1);
                nameDataTournois = rs.getString(2);
                codeDataTournois = rs.getString(3);
                String[] row = {idData, nameDataTournois, codeDataTournois};
                model.addRow(row);
            }

        }
    }

    public void afficherDataBaseScores(PreparedStatement pst) throws SQLException {
        ResultSet rs = pst.executeQuery();
        ResultSetMetaData rsmd = rs.getMetaData();
        DefaultTableModel model = new DefaultTableModel(new Object[]{"Vainqueur", "Finaliste", "Set 1", "Set 2", "Set 3", "Set 4", "Set 5"}, 0);
        tableScores.setModel(model);

        String vainqueur, finaliste, set1, set2, set3, set4, set5;

        while (rs.next()) {
            vainqueur = rs.getString(1);
            finaliste = rs.getString(2);
            set1 = rs.getString(3);
            set2 = rs.getString(4);
            set3 = rs.getString(5);
            set4 = rs.getString(6);
            set5 = rs.getString(7);

            String[] row = {vainqueur, finaliste, set1, set2, set3, set4, set5};
            model.addRow(row);
        }
    }


    public void afficherDataBaseMatchs(PreparedStatement pst) throws SQLException {
        ResultSet rs = pst.executeQuery();
        ResultSetMetaData rsmd = rs.getMetaData();
        DefaultTableModel model = new DefaultTableModel(new Object[]{"ID_MATCH", "Nom vainqueur", "Prénom vainqueur", "Sexe vainqueur"}, 0);
        tableMatchs.setModel(model);

        while (rs.next()) {
            // Ajout d'une ligne à la table des matchs avec les informations des vainqueurs
            model.addRow(new Object[]{rs.getLong("ID"), rs.getString("NOM"), rs.getString("PRENOM"), rs.getString("SEXE")});
        }
    }

    public void afficherDataBaseMatchsFinaliste(PreparedStatement pst) throws SQLException {
        ResultSet rs = pst.executeQuery();
        ResultSetMetaData rsmd = rs.getMetaData();
        DefaultTableModel model = new DefaultTableModel(new Object[]{"ID_MATCH", "Nom finaliste", "Prénom finaliste", "Sexe finaliste"}, 0);
        tableMatchs.setModel(model);

        while (rs.next()) {
            model.addRow(new Object[]{rs.getLong("ID"), rs.getString("NOM"), rs.getString("PRENOM"), rs.getString("SEXE")});
        }
    }

    public TennisApp() {
        Connect();


        afficherScores.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tableScores.setModel(new DefaultTableModel());
                try {
                    Connect();
                    pst = con.prepareStatement(
                            "SELECT j1.NOM AS NomVainqueur, j2.NOM AS NomFinaliste, s.SET_1, s.SET_2, s.SET_3, s.SET_4, s.SET_5 " +
                                    "FROM MATCH_TENNIS m " +
                                    "JOIN JOUEUR j1 ON m.ID_VAINQUEUR = j1.ID " +
                                    "JOIN JOUEUR j2 ON m.ID_FINALISTE = j2.ID " +
                                    "JOIN SCORE_VAINQUEUR s ON m.ID = s.ID_MATCH"
                    );
                    afficherDataBaseScores(pst);
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
        });

        btnAddPlayer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                lastName = txtLastName.getText();
                firstName = txtFirstName.getText();
                gender = txtGender.getText();

                if (lastName.equals("") || firstName.equals("") || gender.equals("")) {
                    JOptionPane.showMessageDialog(null, "Veuillez remplir tous les champs");
                } else if (!((gender.equals("F") || (gender.equals("H"))))) {
                    JOptionPane.showMessageDialog(null, "H ou F");
                } else {
                    try {
                        pst = con.prepareStatement("INSERT INTO JOUEUR(NOM, PRENOM, SEXE)values(?,?,?)");
                        pst.setString(1, lastName);
                        pst.setString(2, firstName);
                        pst.setString(3, gender);
                        pst.executeUpdate();
                        JOptionPane.showMessageDialog(null, "Player Added !");

                        txtLastName.setText("");
                        txtFirstName.setText("");
                        txtGender.setText("");
                    } catch (SQLException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });

        btnUpdatePlayer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                lastName = txtLastName.getText();
                firstName = txtFirstName.getText();
                gender = txtGender.getText();
                id = txtId.getText();

                DefaultTableModel model = (DefaultTableModel) tblData.getModel();

                if (lastName.equals("") || firstName.equals("") || gender.equals("")) {
                    JOptionPane.showMessageDialog(null, "Veuillez remplir tous les champs");
                } else if (!((gender.equals("F") || (gender.equals("H"))))) {
                    JOptionPane.showMessageDialog(null, "H ou F");
                } else {
                    try {

                        pst = con.prepareStatement("UPDATE JOUEUR SET NOM = ?,PRENOM = ?,SEXE= ? where ID = ?");
                        pst.setString(1, lastName);
                        pst.setString(2, firstName);
                        pst.setString(3, gender);
                        pst.setString(4, id);

                        pst.executeUpdate();
                        JOptionPane.showMessageDialog(null, "Joueur modifié !");

                        txtLastName.setText("");
                        txtFirstName.setText("");
                        txtGender.setText("");
                        txtId.setText("");
                        txtId.requestFocus();


                    } catch (SQLException e1) {

                        e1.printStackTrace();
                    }

                }

            }
        });


        btnDeletePlayer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                id = txtId.getText();


                try {
                    PreparedStatement pst2;
                    PreparedStatement pst1;
                    System.out.println("Delete success");
                    pst = con.prepareStatement(" DELETE sV FROM score_vainqueur AS sV INNER JOIN match_tennis AS m ON sV.ID_MATCH = m.ID where m.ID_VAINQUEUR = ? OR m.ID_FINALISTE = ?");
                    pst.setString(1, id);
                    pst.setString(2, id);
                    pst.executeUpdate();
                    JOptionPane.showMessageDialog(null, "Joueur supprimé !");

                    pst1 = con.prepareStatement(" DELETE m FROM match_tennis AS m where m.ID_VAINQUEUR = ? OR m.ID_FINALISTE = ?");
                    pst1.setString(1, id);
                    pst1.setString(2, id);
                    pst1.executeUpdate();

                    pst2 = con.prepareStatement(" DELETE j FROM joueur AS j where id = ?");
                    pst2.setString(1, id);
                    pst2.executeUpdate();

                    txtLastName.setText("");
                    txtFirstName.setText("");
                    txtGender.setText("");
                    txtLastName.requestFocus();
                    txtId.setText("");

                } catch (SQLException e1) {

                    e1.printStackTrace();
                }
            }
        });


        searchAllButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                tblData.setModel(new DefaultTableModel());

                try {
                    String s = txtSearch.getText();
                    pst = con.prepareStatement("SELECT * FROM JOUEUR WHERE ID=? OR NOM=? OR PRENOM=?");
                    pst.setString(1, s);
                    pst.setString(2, s);
                    pst.setString(3, s);
                    ResultSet rs = pst.executeQuery();


                    if (rs.next() == true) {
                        id = rs.getString(1);
                        lastName = rs.getString(2);
                        firstName = rs.getString(3);
                        gender = rs.getString(4);

                        txtLastName.setText(lastName);
                        txtFirstName.setText(firstName);
                        txtGender.setText(gender);
                        txtId.setText(id);
                        afficherDataBase(pst);

                    } else {
                        txtLastName.setText("");
                        txtFirstName.setText("");
                        txtGender.setText("");
                        txtId.setText("");
                        JOptionPane.showMessageDialog(null, "Joueur inconnu");

                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });

        afficherListeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //clear table
                tblData.setModel(new DefaultTableModel());
                try {
                    Connect();

                    if ((hommeCheckBox.isSelected() && femmeCheckBox.isSelected()) || (!hommeCheckBox.isSelected() && !femmeCheckBox.isSelected())) {
                        pst = con.prepareStatement("SELECT * FROM JOUEUR");
                    } else if (hommeCheckBox.isSelected()) {
                        pst = con.prepareStatement("SELECT * FROM JOUEUR WHERE SEXE='H'");
                    } else if (femmeCheckBox.isSelected()) {
                        pst = con.prepareStatement("SELECT * FROM JOUEUR WHERE SEXE='F'");
                    }
                    afficherDataBase(pst);
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
        });

        afficherTournois.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tableDataTournois.setModel(new DefaultTableModel());
                try {
                    Connect();
                    pst = con.prepareStatement("SELECT * FROM TOURNOI");
                    afficherDataBaseTournois(pst);
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
        });


        ajouterButtonTournois.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                nomTournois = txtNomTournois.getText();
                codeTournois = txtCodeTournois.getText();

                if (nomTournois.equals("") || codeTournois.equals("")) {
                    JOptionPane.showMessageDialog(null, "Veuillez remplir tous les champs");
                } else if (codeTournois.length() != 2) {
                    JOptionPane.showMessageDialog(null, "Le code doit comporter 2 lettres uniquement");
                } else {
                    try {
                        pst = con.prepareStatement("INSERT INTO TOURNOI(NOM, CODE)values(?,?)");
                        pst.setString(1, nomTournois);
                        pst.setString(2, codeTournois);
                        pst.executeUpdate();
                        JOptionPane.showMessageDialog(null, "Tournoi ajouté !");

                        txtNomTournois.setText("");
                        txtCodeTournois.setText("");
                    } catch (SQLException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });

        editerButtonTournois.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                nomTournois = txtNomTournois.getText();
                codeTournois = txtCodeTournois.getText();
                idTournois = txtIdTournois.getText();

                DefaultTableModel model = (DefaultTableModel) tableDataTournois.getModel();
//                model.setValueAt(txtIdTournois.getText(), tableDataTournois.getSelectedRow(), 0);
//                model.setValueAt(txtNomTournois.getText(), tableDataTournois.getSelectedRow(), 1);
//                model.setValueAt(txtCodeTournois.getText(), tableDataTournois.getSelectedRow(), 2);


                if (nomTournois.equals("") || codeTournois.equals("")) {
                    JOptionPane.showMessageDialog(null, "Veuillez remplir tous les champs");
                } else if (codeTournois.length() != 2) {
                    JOptionPane.showMessageDialog(null, "Le code doit comporter 2 lettres uniquement");
                } else {
                    try {
                        pst = con.prepareStatement("UPDATE TOURNOI SET NOM = ?,CODE = ? where ID = ?");
                        pst.setString(1, nomTournois);
                        pst.setString(2, codeTournois);
                        pst.setString(3, idTournois);
                        pst.executeUpdate();
                        JOptionPane.showMessageDialog(null, "Tournoi modifié !");

                        txtNomTournois.setText("");
                        txtCodeTournois.setText("");
                        txtIdTournois.setText("");
                        txtIdTournois.requestFocus();


                    } catch (SQLException e1) {

                        e1.printStackTrace();
                    }
                }
            }
        });


        txtSearchTournois.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                Connect();
                tableDataTournois.setModel(new DefaultTableModel());
                DefaultTableModel model = (DefaultTableModel) tableDataTournois.getModel();

                String entreUser = txtSearchTournois.getText();


                System.out.println("try good");
                try {
                    PreparedStatement pst = con.prepareStatement("SELECT ID, NOM, CODE FROM TOURNOI WHERE NOM LIKE ? OR CODE LIKE ? OR ID LIKE ?");
                    pst.setString(1, "%" + entreUser + "%");
                    pst.setString(2, entreUser + "%");
                    pst.setString(3, entreUser + "%");
                    ResultSet rs = pst.executeQuery();
                    ResultSetMetaData rsmd = rs.getMetaData();

                    int cols = rsmd.getColumnCount();
                    String[] colName = new String[cols];
                    for (int i = 0; i < cols; i++) {
                        colName[i] = rsmd.getColumnName(i + 1);
                        model.setColumnIdentifiers(colName);

                        String idData, lastNameData, firstNameData, sexeData;

                        //read row one by one & add in the table
                        while (rs.next()) {
                            idTournois = rs.getString(1);
                            nomTournois = rs.getString(2);
                            codeTournois = rs.getString(3);
                            String[] row = {idTournois, nomTournois, codeTournois};
                            model.addRow(row);
                        }
                    }
                } catch (SQLException e1) {

                    e1.printStackTrace();
                }
            }
        });

        tableDataTournois.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                int ligne = tableDataTournois.getSelectedRow();
                idTournois = tableDataTournois.getModel().getValueAt(ligne, 0).toString();
                nomTournois = tableDataTournois.getModel().getValueAt(ligne, 1).toString();
                codeTournois = tableDataTournois.getModel().getValueAt(ligne, 2).toString();


                txtNomTournois.setText(nomTournois);
                txtCodeTournois.setText(codeTournois);
                txtIdTournois.setText(idTournois);
            }

        });

        supprimerButtonTournois.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                idTournois = txtIdTournois.getText();

                try {
                    PreparedStatement pst4;
                    pst4 = con.prepareStatement(" DELETE sV FROM score_vainqueur AS sV " +
                            "INNER JOIN match_tennis AS m ON sV.ID_MATCH = m.ID " +
                            "INNER JOIN epreuve E ON M.ID_EPREUVE = E.ID WHERE E.ID_TOURNOI = ?");
                    pst4.setString(1, idTournois);
                    pst4.executeUpdate();

                    PreparedStatement pst3;
                    pst3 = con.prepareStatement("DELETE M from match_tennis M INNER JOIN epreuve E ON M.ID_EPREUVE = E.ID WHERE E.ID_TOURNOI = ?");
                    pst3.setString(1, idTournois);
                    pst3.executeUpdate();

                    PreparedStatement pst2;
                    pst2 = con.prepareStatement("DELETE E from epreuve E where id_tournoi = ?");
                    pst2.setString(1, idTournois);
                    pst2.executeUpdate();


                    pst = con.prepareStatement(" DELETE T FROM TOURNOI T where id = ?");
                    pst.setString(1, idTournois);
                    pst.executeUpdate();


                    JOptionPane.showMessageDialog(null, "Tournoi supprimé");

                    txtIdTournois.setText("");
                    txtNomTournois.setText("");
                    txtCodeTournois.setText("");

                } catch (SQLException e1) {

                    e1.printStackTrace();
                }
            }
        });


        tblData.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                int ligneJoueur = tblData.getSelectedRow();
                id = tblData.getModel().getValueAt(ligneJoueur, 0).toString();
                lastName = tblData.getModel().getValueAt(ligneJoueur, 1).toString();
                firstName = tblData.getModel().getValueAt(ligneJoueur, 2).toString();
                gender = tblData.getModel().getValueAt(ligneJoueur, 3).toString();

                txtId.setText(id);
                txtFirstName.setText(firstName);
                txtLastName.setText(lastName);
                txtGender.setText(gender);
            }
        });

        afficherMatchs.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tableMatchs.setModel(new DefaultTableModel());
                try {
                    Connect();
                    pst = con.prepareStatement(
                            "SELECT MATCH_TENNIS.ID, JOUEUR.NOM, JOUEUR.PRENOM, JOUEUR.SEXE " +
                                    "FROM MATCH_TENNIS " +
                                    "JOIN JOUEUR ON JOUEUR.ID = MATCH_TENNIS.ID_VAINQUEUR " +
                                    "ORDER BY MATCH_TENNIS.ID");

                    afficherDataBaseMatchs(pst);
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
        });

        afficherFinalistes.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tableMatchs.setModel(new DefaultTableModel());
                try {
                    Connect();
                    pst = con.prepareStatement(
                            "SELECT MATCH_TENNIS.ID, JOUEUR.NOM, JOUEUR.PRENOM, JOUEUR.SEXE " +
                                    "FROM MATCH_TENNIS " +
                                    "JOIN JOUEUR ON JOUEUR.ID = MATCH_TENNIS.ID_FINALISTE " +
                                    "ORDER BY MATCH_TENNIS.ID");

                    afficherDataBaseMatchsFinaliste(pst);
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
        });

        searchMatchs.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                Connect();
                tableMatchs.setModel(new DefaultTableModel());
                DefaultTableModel model = (DefaultTableModel) tableMatchs.getModel();
                String entreeUser = searchMatchs.getText();
                try {
                    PreparedStatement pst = con.prepareStatement("SELECT MATCH_TENNIS.ID, JOUEUR.NOM, JOUEUR.PRENOM, JOUEUR.SEXE FROM MATCH_TENNIS JOIN JOUEUR ON JOUEUR.ID = MATCH_TENNIS.ID_VAINQUEUR AND MATCH_TENNIS.ID_FINALISTE WHERE NOM LIKE ? OR PRENOM LIKE ? ORDER BY JOUEUR.PRENOM");

                    pst.setString(1, "%" + entreeUser + "%");
                    pst.setString(2, entreeUser + "%");
                    ResultSet rs = pst.executeQuery();
                    ResultSetMetaData rsmd = rs.getMetaData();

                    int cols = rsmd.getColumnCount();
                    String[] colName = new String[cols];
                    for (int i = 0; i < cols; i++) {
                        colName[i] = rsmd.getColumnName(i + 1);
                        model.setColumnIdentifiers(colName);

                        while (rs.next()) {
                            idMatchs = rs.getString(1);
                            nomMatchs = rs.getString(2);
                            prenomMatchs = rs.getString(3);
                            sexeMatchs = rs.getString(4);
                            String[] row = {idMatchs, nomMatchs, prenomMatchs, sexeMatchs};
                            model.addRow(row);
                        }
                    }
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
        });


        searchVicDef.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                Connect();
                tableVicDef.setModel(new DefaultTableModel());
                DefaultTableModel model = (DefaultTableModel) tableVicDef.getModel();
                String entreeUser = searchVicDef.getText();
                try {
                    PreparedStatement pst = con.prepareStatement("SELECT JOUEUR.ID, JOUEUR.NOM, JOUEUR.PRENOM, JOUEUR.SEXE, COUNT(CASE WHEN JOUEUR.ID = MATCH_TENNIS.ID_VAINQUEUR THEN 1 ELSE NULL END) AS VICTOIRES, COUNT(CASE WHEN JOUEUR.ID = MATCH_TENNIS.ID_FINALISTE THEN 1 ELSE NULL END) AS DEFAITES FROM JOUEUR JOIN MATCH_TENNIS ON JOUEUR.ID = MATCH_TENNIS.ID_VAINQUEUR OR JOUEUR.ID = MATCH_TENNIS.ID_FINALISTE WHERE JOUEUR.NOM LIKE ? OR JOUEUR.PRENOM LIKE ? GROUP BY JOUEUR.ID ORDER BY JOUEUR.PRENOM");

                    //comptage pour déterminer le nombre de victoires pour chaque joueur.  CASE WHEN permet de déterminer si le joueur a gagné un match en comparant son identifiant avec l'identifiant du vainqueur d'un match de tennis. Puis nb défaite.
                    // Récupère les données JOUEUR et de la table MATCH_TENNIS avec Jointure
                    // Filtre nom + prénom
                    // Regroupe en fonction de l'id du joueur et trie par ordre alphabétique

                    pst.setString(1, "%" + entreeUser + "%");
                    pst.setString(2, entreeUser + "%");
                    ResultSet rs = pst.executeQuery();
                    ResultSetMetaData rsmd = rs.getMetaData();

                    int cols = rsmd.getColumnCount();
                    String[] colName = new String[cols];
                    for (int i = 0; i < cols; i++) {
                        colName[i] = rsmd.getColumnName(i + 1);
                        model.setColumnIdentifiers(colName);
                    }
                    while (rs.next()) {
                        String idJoueur = rs.getString("ID");
                        String nomJoueur = rs.getString("NOM");
                        String prenomJoueur = rs.getString("PRENOM");
                        String sexeJoueur = rs.getString("SEXE");
                        String nbVictoires = rs.getString("VICTOIRES");
                        String nbDefaites = rs.getString("DEFAITES");
                        String[] row = {idJoueur, nomJoueur, prenomJoueur, sexeJoueur, nbVictoires, nbDefaites};
                        model.addRow(row);
                    }
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
        });

        searchEpreuves.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                Connect();
                tableEpreuves.setModel(new DefaultTableModel());
                DefaultTableModel model = (DefaultTableModel) tableEpreuves.getModel();
                String entreeUser = searchEpreuves.getText();
                try {
                    PreparedStatement pst = con.prepareStatement("SELECT JOUEUR.NOM, JOUEUR.PRENOM, EPREUVE.ANNEE, EPREUVE.TYPE_EPREUVE FROM JOUEUR JOIN MATCH_TENNIS ON JOUEUR.ID = MATCH_TENNIS.ID_VAINQUEUR OR JOUEUR.ID = MATCH_TENNIS.ID_FINALISTE JOIN EPREUVE ON EPREUVE.ID = MATCH_TENNIS.ID_EPREUVE WHERE JOUEUR.NOM LIKE ? OR JOUEUR.PRENOM LIKE ? GROUP BY JOUEUR.ID, EPREUVE.ANNEE, EPREUVE.TYPE_EPREUVE ORDER BY EPREUVE.ANNEE");

                    pst.setString(1, "%" + entreeUser + "%");
                    pst.setString(2, entreeUser + "%");
                    ResultSet rs = pst.executeQuery();
                    ResultSetMetaData rsmd = rs.getMetaData();

                    int cols = rsmd.getColumnCount();
                    String[] colName = new String[cols];
                    for (int i = 0; i < cols; i++) {
                        colName[i] = rsmd.getColumnName(i + 1);
                    }
                    model.setColumnIdentifiers(new String[]{"Nom", "Prénom", "Année", "Type d'épreuve"});
                    while (rs.next()) {
                        String nomJoueur = rs.getString("NOM");
                        String prenomJoueur = rs.getString("PRENOM");
                        String anneeEpreuve = rs.getString("ANNEE");
                        String typeEpreuve = rs.getString("TYPE_EPREUVE");
                        String[] row = {nomJoueur, prenomJoueur, anneeEpreuve, typeEpreuve};
                        model.addRow(row);
                    }
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
        });

    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("TennisApp");
        frame.setContentPane(new TennisApp().panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setSize(900, 800);
        frame.setVisible(true);
    }
}