/*
 * Lausekielinen Ohjelmointi 2
 * Harjoitustyö, BimEdit
 * Oskari Kansanen (oskari.kansanen@tuni.fi)
 * Viimeksi muokattu: 14.12.2019
 */

import java.util.Scanner;
import java.io.*;

public class BimEdit {
    /*
     * Tässä metodissa otetaan käyttäjän komennot vastaan, ja kutsutaan sen mukaisia metodeja
     * loadedTable: main-metodin lataama taulukko
     * symbols: taulukon merkit yhdessä listassa
     * ifEcho: tarkistus, onko käyttäjä määrännyt komentoparametreissään echon
     */
    public static void parseCommands(char[][] loadedTable,char[] symbols,boolean ifEcho) {
        // Luodaan while-luuppia ylläpitävä muuttuja ja lukija-olio
        boolean jatketaan = true;
        Scanner lukija = new Scanner(System.in);
        char[][] varmuuskopio = loadedTable;
        
        while (jatketaan) {
            System.out.println("print/info/invert/dilate/erode/load/quit");
            String command = lukija.nextLine();
            
            if (ifEcho) {
                System.out.println(command);
            }
            
            if (command.equals("invert")) {
                loadedTable = doInvert(loadedTable,symbols);
            }
            else if (command.equals("print")) {
                print2d(loadedTable);
            }
            else if (command.equals("info")) {
                getInfo(loadedTable,symbols);
            }
            else if(command.equals("load")) {
                loadedTable = varmuuskopio;
            }
            else if (command.equals("quit")) {
                System.out.println("Bye, see you soon!");
                jatketaan = false;
            }
            else {
                try {
                    int size = 0;
                    String[] splitCommand = command.split(" ");
                    if (splitCommand[0].equals("dilate") && splitCommand[1].length() == 1) {
                        size = Integer.parseInt(splitCommand[1]);
                        if (size % 2 != 0 && size > 2) {
                            System.out.println("yes");
                        }
                    }
                    else if (splitCommand[0].equals("erode") && splitCommand[1].length() == 1) {
                        size = Integer.parseInt(splitCommand[1]);
                        if (size % 2 != 0 && size > 2) {
                            System.out.println("jis");
                        }
                    }
                    else {
                        System.out.println("Invalid command!");
                    }
                }
                catch (Exception e) {
                    System.out.println("Invalid command!");
                }
            }
        }
    }
    
    /*
     * Tässä metodissa vaihdetaan merkit päittäin, eli tehdään kuvasta "negatiivi"
     * loadedTable: ASCII-kuva, josta negatiiviversio tehdään
     * symbols: taulun kaksi merkkiä, jotka vaihdetaan päittäin
     * return-arvo on uusi, negatiivinen kuva
     */
    public static char[][] doInvert(char[][] loadedTable,char[] symbols) {
        // Luodaan tyhjä lista, jolle voidaan pistää negatiiviversio
        char[][] toReturn = new char[loadedTable.length][loadedTable[0].length];
        
        // Käydään lista läpi merkki merkiltä, ja muutetaan merkki aina toiseksi
        for (int ind3 = 0; ind3 < loadedTable.length; ind3++) {
            for (int ind4 = 0; ind4 < loadedTable[0].length; ind4++) {
                if (loadedTable[ind3][ind4] == symbols[0]) {
                    toReturn[ind3][ind4] = symbols[1];
                }
                else {
                    toReturn[ind3][ind4] = symbols[0];
                }
            }
        }
        
        // Palautetaan uusi kuva
        return toReturn;
        
    }
    
    /*
     * Tässä metodissa haetaan taulukon tiedot, ja printataan ne
     * loadedTable: itse taulu
     * symbols: taulun symbolit
     */
    public static void getInfo(char[][] loadedTable,char[] symbols) {
        // Symbolien määrät muuttujina
        int amountOfSymbol1 = 0;
        int amountOfSymbol2 = 0;
        
        // Lasketaan symbolit kahdella for-luupilla
        for (int x = 0; x < loadedTable.length; x++) {
            for (int z = 0; z < loadedTable[0].length; z++) {
                if (loadedTable[x][z] == symbols[0]) {
                    amountOfSymbol1++;
                }
                // Jos kyseessä ei ole symboli1, on kyseessä symboli2
                else {
                    amountOfSymbol2++;
                }
            }
        }
        
        // Tiedot printataan siististi
        System.out.println(loadedTable.length + " x " + loadedTable[0].length);    
        System.out.println(symbols[0] + " " + amountOfSymbol1);
        System.out.println(symbols[1] + " " + amountOfSymbol2);
    }
    
    /*
     * Tässä metodissa printataan ladattu taulu
     * loadedTable: kyseinen taulu
     */
    public static void print2d(char[][] loadedTable) {
        // Printataan kahdella sisäkkäisellä for-luupilla
        // Rivit
        for (int ind1 = 0; ind1 < loadedTable.length; ind1++) {
            // Sarakkeet
            for (int ind2 = 0; ind2 < loadedTable[0].length; ind2++) {
                System.out.print(loadedTable[ind1][ind2]);
            }
            // Rivinvaihto
            System.out.println();
        }
    }
    
    /*
     * Metodissa ladataan taulukko toReturn-nimiseen muuttujaan tiedostosta
     * jokaisesta virheestä, esim NullPointerExceptionista ja StringIndexOutOfBoundsExceptionista palautetaan null
     * fileName: tiedoston nimi, josta taulukko nostetaan
     * symbols: tiedostosta nostetaan määritellyt symbolit, eli taustamerkki ja edustamerkki
     * palautettava arvo on tosiaan virheen sattuessa null, tai sitten generoitu toReturn-taulukko.
     */
    public static char[][] load(String fileName,char[] symbols) {
        // Luodaan taulukko kuvan tallentamista varten
        char[][] toReturn = null;
        // Tässä on apumuuttujat taulukon koon määrittämiseksi
        int rows = 0;
        int columns = 0;
        
        // Luodaan tämä tässä, jotta sitä voi myös käyttää Catch-lauseessa
        Scanner tiedostonLukija = null;
        try {
            // Tiedosto-olio
            File file = new File(fileName);
            // Tiedosto-olio liitetään lukijaan
            tiedostonLukija = new Scanner(file);
            
            // Kaksi apumuuttujaa while-luuppiin
            int i = 0;
            int j = 0;
            while (tiedostonLukija.hasNextLine()) {
                // Rivien tarkasteluun tarkoitettu muuttuja
                String line = tiedostonLukija.nextLine();
                
                // Käydään i:n indeksit läpi
                if (i == 0) {
                    // Rivien määrä
                    rows = Integer.parseInt(line);
                    i++;
                }
                else if (i == 1) {
                    // Sarakkeiden määrä
                    columns = Integer.parseInt(line);
                    // Tehdään tarkistus, onko taulukko oikean kokoinen
                    if (rows < 3 || columns < 3) {
                        return null;
                    }
                    // Päivitetään listan koko
                    toReturn = new char[rows][columns];
                    i++;
                }
                else if (i == 2) {
                    // Merkki 1
                    symbols[0] = line.charAt(0);
                    i++;
                }
                else if (i == 3) {
                    // Merkki 2
                    symbols[1] = line.charAt(0);
                    i++;
                }
                else if (i >= 4) {
                    // Kaikki loput rivit ovat itse kuvaa
                    for (int k = 0; k < toReturn[0].length; k++) {
                        // Tarkastetaan, onko symbolit oikeita
                        if (line.charAt(k) == symbols[0] || line.charAt(k) == symbols[1]) {
                            toReturn[j][k] = line.charAt(k);
                        }
                        else {
                            return null;
                        }
                    }
                    j++;
                    i++;
                }
            }
            // Suljetaan lukija ja palautetaan char[][] taulukko
            tiedostonLukija.close();
            return toReturn;
        }
        catch (Exception e) {
            // Suljetaan tiedostonlukija, jos se on tarpeen
            if (tiedostonLukija != null) {
                tiedostonLukija.close();
            }
            // Palautetaan null virheen merkiksi    
            return null;
        }
    }
    
    public static void main(String[] args) {
        // Tulostetaan tervehdys
        System.out.println("-----------------------");
        System.out.println("| Binary image editor |");
        System.out.println("-----------------------");
        
        // Lähdetään katsomaan komentoparametrejä, ja pyritään lataamaan taulukko, jota voi käyttää
        try {
            // Luodaan muuttuja tiedoston nimeä varten
            String fileName = null;
            // Luodaan muuttuja echoa varten
            boolean ifEcho = false;
            
            // Tarkistetaan, onko parametrejä liikaa
            if (args == null || (args.length > 1 && !args[1].equals("echo"))) {
                System.out.println("Invalid command-line argument!");
                System.out.println("Bye, see you soon.");
            }
            else {
                // Nyt asetetaan tiedoston nimi parametrien mukaisesti
                fileName = args[0];
                // Luodaan lista merkeille
                char[] symbols = new char[2];
                // Kutsutaan load-funktiota ja tallennetaan se muuttujaan
                char[][] loadedTable = load(fileName,symbols);
                
                // Tarkistetaan, onko Echo mukana parametreissä
                if (args.length == 2 && args[1].equals("echo")) {
                    ifEcho = true;
                }
                
                // Tarkastellaan paluuarvoa. Jos tiedosto on ollut jotenkin virheellinen,
                // Otetaan siitä koppi tässä null-returnarvon avulla
                if (loadedTable == null) {
                    System.out.println("Invalid image file!");
                    System.out.println("Bye, see you soon.");
                }
                else {
                    // jos kaikki on OK, niin lähdetään seikkailemaan käyttäjän komentojen kanssa
                    parseCommands(loadedTable,symbols,ifEcho);
                }
            }
        }
        catch (Exception e) {
            System.out.println("Invalid command-line argument!");
            System.out.println("Bye, see you soon.");
        }
    }
}