/*
 * Lausekielinen Ohjelmointi 2
 * Harjoitustyö, BimEdit
 * Oskari Kansanen (oskari.kansanen@tuni.fi)
 * Viimeksi muokattu: 18.12.2019
 */

// Otetaan käyttöön Scanner-luokka, jotta voidaan lukea tiedostoja ja käyttäjän komentoja
import java.util.Scanner;
// Tiedoston lukua varten
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
        // Luodaan inverttiä varten muuttuja, jonka avulla invertattuja kuvia käsitellään
        boolean ifInvert = false;
        // Scanner-olio, jotta saadaan käyttäjän komennot
        Scanner lukija = new Scanner(System.in);
        // Luodaan listasta kopio, helppoa lataamista varten
        char[][] varmuuskopio = doLoad(loadedTable);
        // Luodaan symboleille omat muuttujat
        char backSymbol = symbols[0];
        char frontSymbol = symbols[1];
        
        // While-luuppi, jonka sisällä käsitellään, ja pyydetään komentoja
        while (jatketaan) {
            System.out.println("print/info/invert/dilate/erode/load/quit?");
            String command = lukija.nextLine();
            
            // Jos echo oli määritelty, komento toistetaan
            if (ifEcho) {
                System.out.println(command);
            }
            
            if (command.equals("invert")) {
                // Invertataan värit
                loadedTable = doInvert(loadedTable,symbols);
                if (ifInvert == true) {
                    ifInvert = false;
                }
                else if (ifInvert == false) {
                    ifInvert = true;
                }
            }
            else if (command.equals("print")) {
                // Printataan kuva
                print2d(loadedTable);
            }
            else if (command.equals("info")) {
                // Haetaan kuvan tiedot
                if (ifInvert == false) {
                    getInfo(loadedTable,backSymbol,frontSymbol);
                }
                else if (ifInvert == true) {
                    getInfo(loadedTable,frontSymbol,backSymbol);
                }
            }
            else if(command.equals("load")) {
                // Ladataan kuva varmuuskopiosta
                loadedTable = varmuuskopio;
                ifInvert = false;
            }
            else if (command.equals("quit")) {
                // Lähretään pois, jos näin käsketään
                System.out.println("Bye, see you soon.");
                jatketaan = false;
            }
            else {
                try {
                    // Katsotaan, onko dilate määritelty oikein
                    int size = 0;
                    String[] splitCommand = command.split(" ");
                    if (splitCommand.length > 2) {
                        System.out.println("Invalid command!");
                    }
                    else if (splitCommand[0].equals("dilate")) {
                        size = Integer.parseInt(splitCommand[1]);
                        if (size % 2 != 0 && size > 2 && size < loadedTable[0].length) {
                            // Dilaten paikka
                            if (ifInvert == false) {
                                loadedTable = doDilate(loadedTable,size,backSymbol,frontSymbol);
                            }
                            else if (ifInvert == true) {
                                loadedTable = doDilate(loadedTable,size,frontSymbol,backSymbol);
                            }
                        }
                        else {
                            System.out.println("Invalid command!");
                        }
                    }
                    // Sama erodeen
                    else if (splitCommand[0].equals("erode")) {
                        size = Integer.parseInt(splitCommand[1]);
                        if (size % 2 != 0 && size > 2 && size < loadedTable[0].length) {
                            // Eroden paikka
                            if (ifInvert == false) {
                                loadedTable = doDilate(loadedTable,size,frontSymbol,backSymbol);
                            }
                            else if (ifInvert == true) {
                                loadedTable = doDilate(loadedTable,size,backSymbol,frontSymbol);
                            }
                        }
                        else {
                            System.out.println("Invalid command!");
                        }
                    }
                    else {
                        System.out.println("Invalid command!");
                    }
                }
                // Jos ei suju, huudetaan invalid commandia
                catch (Exception e) {
                    System.out.println("Invalid command!");
                }
            }
        }
    }
    
    /*
     * Tämä metodi tekee kopion määritellystä taulusta
     * loadedTable: alussa ladattu kuva
     * Palautetaan kopio taulusta
     */
    public static char[][] doLoad(char[][] loadedTable) {
        // Alustetaan palautettava muuttuja
        char[][] loadReturn = new char[loadedTable.length][loadedTable[0].length];
        
        for (int i = 0; i < loadedTable.length; i++) {
            for (int n = 0; n < loadedTable[0].length; n++) {
                loadReturn[i][n] = loadedTable[i][n];
            }
        }
        return loadReturn;
    }
    
    /*
     * Tässä metodissa luodaan size x size neliö, josta tarkastetaan etu tai takamerkkejä
     * loadedTable: ladattu kuva
     * size: neliön koko
     * frontSymbol: etusymboli
     * backSymbol: takasymboli
     * i: kohta, jossa ollaan dilaten for-luupissa
     * n: --::--
     */
    public static boolean changeSymbol(char[][] loadedTable,int size,char frontSymbol,char backSymbol,int i,int n) {
        int paikka = size/2;
                
        for (int ind1 = i - paikka; ind1 <= i + paikka; ind1++) {
            for (int ind2 = n - paikka; ind2 <= n + paikka; ind2++) {
                if (loadedTable[ind1][ind2] == backSymbol) {
                    return true;
                }
            }
        }
        return false;
    }
    
    /*
     * Tässä metodissa kasvatetaan tai pienennetään kuvaa, käyttäjän määrittelemän koon mukaan.
     * loadedTable: ladattu kuva
     * size: suurennuksen, tai pienennyksen koko
     * frontSymbol: etusymboli
     * backsymbol: takasymboli
     * return-arvo on suurennettu tai pienennetty kuva
     */
    public static char[][] doDilate(char[][] loadedTable,int size,char frontSymbol,char backSymbol) {
        // Luodaan kopio taulusta, jota voi sitten muokata
        char[][] dilateReturn = doLoad(loadedTable);
        
        // Haetaan ensimmäinen indeksi
        int firstInd = size/2;
        
        // Käytän tässä i:tä ja n:nää oman selvyyden vuoksi
        for (int i = firstInd; i < loadedTable.length - firstInd; i++) {
            for (int n = firstInd; n < loadedTable[0].length - firstInd; n++) {
                // Kutsutaan metodia, joka tarkastaa size x size alueen, merkin ympäriltä
                boolean vaihdetaanko = changeSymbol(loadedTable,size,frontSymbol,backSymbol,i,n);
                if (vaihdetaanko == true) {
                    dilateReturn[i][n] = backSymbol;
                }
            }
        }
        // Palautetaan uusi kuva
        return dilateReturn;
    }
    
    /*
     * Tässä metodissa vaihdetaan merkit päittäin, eli tehdään kuvasta "negatiivi"
     * loadedTable: ASCII-kuva, josta negatiiviversio tehdään
     * symbols: taulun kaksi merkkiä, jotka vaihdetaan päittäin
     * return-arvo on uusi, negatiivinen kuva
     */
    public static char[][] doInvert(char[][] loadedTable,char[] symbols) {
        // Luodaan tyhjä lista, jolle voidaan pistää negatiiviversio
        char[][] invertReturn = new char[loadedTable.length][loadedTable[0].length];
        
        // Käydään lista läpi merkki merkiltä, ja muutetaan merkki aina toiseksi
        for (int ind3 = 0; ind3 < loadedTable.length; ind3++) {
            for (int ind4 = 0; ind4 < loadedTable[0].length; ind4++) {
                if (loadedTable[ind3][ind4] == symbols[0]) {
                    invertReturn[ind3][ind4] = symbols[1];
                }
                else {
                    invertReturn[ind3][ind4] = symbols[0];
                }
            }
        }
        // Palautetaan uusi kuva
        return invertReturn;
        
    }
    
    /*
     * Tässä metodissa haetaan taulukon tiedot, ja printataan ne
     * loadedTable: itse taulu
     * symbols: taulun symbolit
     */
    public static void getInfo(char[][] loadedTable,char backSymbol,char frontSymbol) {
        // Symbolien määrät muuttujina
        int amountOfSymbol1 = 0;
        int amountOfSymbol2 = 0;
        
        // Lasketaan symbolit kahdella for-luupilla
        for (int x = 0; x < loadedTable.length; x++) {
            for (int z = 0; z < loadedTable[0].length; z++) {
                if (loadedTable[x][z] == backSymbol) {
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
        System.out.println(backSymbol + " " + amountOfSymbol1);
        System.out.println(frontSymbol + " " + amountOfSymbol2);
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
                    // Tarkistetaan, onko teksti liian leveä
                    if (line.length() > columns) {
                        return null;
                    }
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
            // Suljetaan lukija
            tiedostonLukija.close();
            // Tarkastetaan taulu
            for (int i1 = 0; i1 < toReturn.length; i1++) {
                for (int i2 = 0; i2 < toReturn[0].length; i2++){
                    if (toReturn[i1][i2] == '\u0000') {
                        return null;
                    }
                }
            }
            // Palautetaan taulu, jos kaikki on ok
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
        // Paetaan kovaa, jos jotain menee vikaan
        catch (Exception e) {
            System.out.println("Invalid command-line argument!");
            System.out.println("Bye, see you soon.");
        }
    }
}