package LibSocket;

import Controller.ControllerView;
import View.WindowClient;

import java.io.*;
import java.net.Socket;
import java.lang.*;




public class LibSocket {

    ControllerView main;

    public static void send(Socket sSocket, String requete)
    {
        try {
            DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(sSocket.getOutputStream()));

            requete = requete + "#)";
            dos.write(requete.getBytes());
            dos.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static String receive(Socket sSocket)
    {
        try {
            DataInputStream dis = new DataInputStream(new BufferedInputStream(sSocket.getInputStream()));

            StringBuilder buffer = new StringBuilder();
            boolean EOT = false;
            while(!EOT) // boucle de lecture byte par byte
            {
                byte b1 = dis.readByte();
                if (b1 == (byte)'#')
                {
                    byte b2 = dis.readByte();
                    if (b2 == (byte)')') EOT = true;
                    else
                    {
                        buffer.append((char)b1);
                        buffer.append((char)b2);
                    }
                }
                else buffer.append((char)b1);
            }
            return buffer.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



}


