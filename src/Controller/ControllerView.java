package Controller;

import LibSocket.LibSocket;
import View.WindowClient;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

import static java.lang.Float.parseFloat;


public class ControllerView implements ActionListener {

    private WindowClient mainWindow; //reference a la view

    private Socket cSocket;

    private int CurrentIdArticle = 0;

    private ArrayList<String> liste = new ArrayList<String>();




    public ControllerView(WindowClient mainWindow)
    {
        this.mainWindow = mainWindow;
        mainWindow.setPublicite("CLIENT JAVA");

    }



    @Override
    public void actionPerformed(ActionEvent e) {

        if(!mainWindow.getNom().isEmpty() && !mainWindow.getMotDePasse().isEmpty()){
            JButton source = (JButton) e.getSource();
            //LOGIN
            if (source.getText().equals("Login")) {
                // Création de la socket et connexion sur le serveur
                try {
                    cSocket = new Socket("192.168.244.130",4444);//192.168.244.130
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                String raison = "";
                if(OVESP_Login(mainWindow.getNom(), mainWindow.getMotDePasse(), mainWindow.isNouveauClientChecked(), raison)){
                    /*if(!raison.isEmpty()){
                        OVESP_Login(mainWindow.getNom(), mainWindow.getMotDePasse(), mainWindow.isNouveauClientChecked(), raison);
                        mainWindow.dialogueErreur("Erreur",raison);
                    }else{
                        mainWindow.dialogueErreur("Erreur", "Erreur Inconnue");
                    }

                }else{
                    mainWindow.dialogueMessage("Connecter", "Re-bonjour cher client");*/

                    mainWindow.LoginOK();
                    Consult(CurrentIdArticle);

                }

            }//if fin login
            if (source.getText().equals("Logout")) {
                String requete = "LOGOUT#1";
                System.out.println("Requete envoye" +requete);

                // ***** Envoi requete + réception réponse **************
                LibSocket.send(cSocket,requete);
                System.out.println("apres send");

                String reponse = LibSocket.receive(cSocket);
                System.out.println("Requete recu" + reponse);

                //PARSING REPONSE
                String[] mots = reponse.split("#"); // LOGIN#

                //System.exit(0);
                mainWindow.LogoutOK();
                try {
                    cSocket.close();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
            if(source.getText().equals(">>>")){
                /*String requete = "CONSULT#" + CurrentIdArticle;
                System.out.println("Requete envoye" +requete);

                // ***** Envoi requete + réception réponse **************
                LibSocket.send(cSocket,requete);
                System.out.println("apres send");

                String reponse = LibSocket.receive(cSocket);

                //PARSING REPONSE
                String[] mots = reponse.split("#"); // LOGIN#*/

                System.out.println(">>>");
                CurrentIdArticle = (CurrentIdArticle+1)%21;


                Consult(CurrentIdArticle);

            }
            if(source.getText().equals("<<<")){
                System.out.println("<<<");
                CurrentIdArticle = (CurrentIdArticle+20)%21;
                Consult(CurrentIdArticle);

            }
            if(source.getText().equals("Acheter")){
                if(mainWindow.getQuantité() == 0)
                    return;
                LibSocket.send(cSocket,"ACHAT#"+(CurrentIdArticle+1)+"#"+mainWindow.getQuantité());

                String reponse = LibSocket.receive(cSocket);

                String[] mots = reponse.split("#");
                System.out.println("Requete recu" + reponse);
                //Requete recu ACHAT#5#1#10.25
                //exemple 5=idArticle 1=demande 10.25 = prix

                if(mots[0].equals("ACHAT"))
                {
                    if(mots[1].equals("0"))
                    {
                        mainWindow.dialogueErreur(mots[0],mots[3]);
                    }
                    else if(!mots[1].equals("-1"))
                    {
                        if(!mots[2].equals("0"))
                        {
                            OVESP_Caddie3();
                            Consult(CurrentIdArticle);

                        }
                        else
                            mainWindow.dialogueErreur(mots[0],"Pas assez de stock");
                    }
                    else
                        mainWindow.dialogueErreur(mots[0],"Not Found");
                }
                else
                    mainWindow.dialogueErreur(mots[0],"Error");


            }
            if(source.getText().equals("Supprimer article")) {
                int i = 0;
                if ((i = mainWindow.getIndiceArticleSelectionne()) != -1) {
                    System.out.println("IndiceArticleSelectionne" + i);

                    LibSocket.send(cSocket,"CANCEL#"+ liste.get(i));

                    String reponse = LibSocket.receive(cSocket);

                    String[] mots = reponse.split("#");
                    System.out.println("Requete recu" + reponse);

                    OVESP_Caddie3();
                    Consult(CurrentIdArticle);

                }else{
                    mainWindow.dialogueErreur("Erreur","il faut selectionner un article");

                }


            }
            if(source.getText().equals("Vider le panier")){
                LibSocket.send(cSocket,"CANCELALL#1");

                String reponse = LibSocket.receive(cSocket);

                String[] mots = reponse.split("#");
                System.out.println("Requete recu" + reponse);
                OVESP_Caddie3();
                Consult(CurrentIdArticle);

            }
            if(source.getText().equals("Payer")){
                LibSocket.send(cSocket,"CONFIRMER");

                String reponse = LibSocket.receive(cSocket);

                String[] mots = reponse.split("#");
                System.out.println("Requete recu" + reponse);

                if(!mots[1].equals("-1"))
                {

                    mainWindow.dialogueMessage("Confirmation de la commande", "Numero de commande : " +mots[1]);
                }
                else
                    mainWindow.dialogueErreur("Erreur", "le caddie est vide");
                mainWindow.videTablePanier();

            }



            } else{
        mainWindow.dialogueErreur("Erreur", "Entrez quelque chose");
    }


    }




    public Boolean OVESP_Login(String user, String password, int nouveauClient, String raison)
    {
        String requete,reponse;
        boolean onContinue = true;

        // ***** Construction de la requete *********************
        requete = "LOGIN#" + user + "#" + password + "#" + nouveauClient;
        System.out.println("Requete envoye" +requete);

        // ***** Envoi requete + réception réponse **************
        LibSocket.send(cSocket, "LOGIN#" + user + "#" + password + "#" + nouveauClient);
        System.out.println("apres send");

        reponse = LibSocket.receive(cSocket);
        System.out.println("Requete recu" + reponse);

        //PARSING REPONSE
        String[] mots = reponse.split("#"); // LOGIN#

        if (mots[1].equals("ok")) {
            //Consult(CurrentIdArticle);
           // mainWindow.LoginOK();
            System.out.println("Login Client ok#" + mots[2]);
            mainWindow.dialogueMessage("Connecter", "Re-bonjour cher client");



        }else{
            //if(mots[1].equals("ko"))
            raison = mots[2];
            System.out.println("Erreur de Login " + raison);
            mainWindow.dialogueErreur("Erreur",raison);

            onContinue = false;
        }

        return onContinue;

    }


    void OVESP_ConsultUpdate()
    {
        String idc = "",nom = "",prix = "",lot = "",image = "";
        OVESP_Consult(CurrentIdArticle,idc,nom,prix,lot,image);
        System.out.println("dans consult update apres consult");
        System.out.println("CurrentIdArticle : " + CurrentIdArticle);
        System.out.println("idc : " + idc);
        System.out.println("prix : " + prix);
        System.out.println("lot : " + lot);
        System.out.println("image : " + image);

        if(!idc.isEmpty())
        {
            if(!nom.isEmpty() && !prix.isEmpty() && !lot.isEmpty() && !image.isEmpty()) {
                prix = prix.replace(".", ",");
                mainWindow.setArticle(nom, parseFloat(prix),Integer.parseInt(lot),image);

            }
        }
    }


    void OVESP_Consult(int id,String idc,String nom,String prix,String lot,String image)
    {
        String requete,reponse;

        // ***** Construction de la requete *********************
        requete = "CONSULT#" + id;
        System.out.println("Requete envoye " +requete);

        // ***** Envoi requete + réception réponse **************
        LibSocket.send(cSocket,requete);
        System.out.println("apres send");

        reponse = LibSocket.receive(cSocket);
        System.out.println("Requete recu " + reponse);


        String[] elements = reponse.split("#");

        //PARSING REPONSE
        String[] mots = reponse.split("#"); // CONSULT#
       /* for (String mot : mots) {
            System.out.println(mot);
        }*/

        // ***** Parsing de la réponse **************************
        if (mots[1].equals("-1")) {
            System.out.println("Pas d'articles");
        }else{
            idc = mots[1];
            nom = mots[2];
            prix = mots[3];
            lot = mots[4];
            image = mots[5];

            System.out.println("DANS OVESP CONSULT");

            System.out.println("idc: " + idc);
            System.out.println("nom: " + nom);
            System.out.println("prix: " + prix);
            System.out.println("lot: " + lot);
            System.out.println("image: " + image);

        }


    }
    private void Consult(int id)
    {

        id++;
        LibSocket.send(cSocket,"CONSULT#"+id);
        System.out.println("Requete envoye CONSULT#"+id);

        String reponse = LibSocket.receive(cSocket);
        System.out.println("Requete recu " + reponse);

        String[] mots = reponse.split("#");

        if (mots[0].equals("CONSULT"))
        {
            if (!mots[1].equals("-1"))
            {
                mainWindow.setArticle(mots[2], parseFloat(mots[3]), Integer.parseInt(mots[4]), mots[5]);
            }
            else
                mainWindow.dialogueErreur(mots[0],mots[2]);
        }
        else
            mainWindow.dialogueErreur(mots[0],mots[2]);
    }




    public void OVESP_Caddie3() {

        String article;

        float prix = 0.0f, totalprix = 0.0f;
        int quantite = 0, i = 0;

        // Envoie de la requête
        mainWindow.videTablePanier();

        LibSocket.send(cSocket,"CADDIE#1");

        String reponse = LibSocket.receive(cSocket);

        System.out.println("Requete recu " + reponse);


        // Vider le panier
        mainWindow.videTablePanier();

        // Parcourir la réponse
        String[] mots = reponse.split("#");
        System.out.println("obhebf" + mots.length );
        liste.clear();

        for (int j = 1; j < mots.length ; j = j+4) {

                // Obtenir l'identifiant de l'article
                try {
                    i = Integer.parseInt(mots[j]);
                    liste.add(mots[j]);


                    System.out.println(liste);

                    System.out.println("Requete recu " + reponse);

                } catch (NumberFormatException e) {
                    // Ignorer l'article
                    continue;
                }

                // Obtenir l'article
                article = mots[j + 1];//.trim

                // Obtenir la quantité
                try {
                    quantite = Integer.parseInt(mots[j + 2]);
                    System.out.println("QUANTITE " + quantite);
                    System.out.println("QUANTITE " + mots[j + 2]);



                } catch (NumberFormatException e) {
                    // Ignorer l'article
                    continue;
                }

                // Obtenir le prix
                try {
                    prix = Float.parseFloat(mots[j + 3]);
                    System.out.println("PRIX " + prix);
                }catch (Exception e){
                    System.out.println("PRIX execp " + mots[j + 3] + e);
                }

                totalprix += prix * quantite;

                // Ajouter l'article au panier
                mainWindow.ajouteArticleTablePanier(article, prix, quantite);


        }
        mainWindow.setTotal(totalprix);

    }
    private void Actualiser_Panier()
    {
        mainWindow.videTablePanier();

        LibSocket.send(cSocket,"CADDIE");

        String reponse = LibSocket.receive(cSocket);

        String[] mots = reponse.split("#");

        int count = Integer.parseInt(mots[2]);
        float total=0;
        for(int i=0;i<count;i++)
        {
            total = total + Float.parseFloat(mots[i*5+7])*Integer.parseInt(mots[i*5+5]);
            System.out.println(reponse);
            mainWindow.ajouteArticleTablePanier(mots[i*5+4], Float.parseFloat(mots[i*5+7]), Integer.parseInt(mots[i*5+5]));
        }

        mainWindow.setTotal(total);

    }




}
