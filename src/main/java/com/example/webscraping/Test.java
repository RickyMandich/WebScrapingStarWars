package com.example.webscraping;

public class Test {
    public static void main(String[] args) {
        String idMessage = Scan.alert("messaggio da modificare", true);
        try{
            ThreadParse.sleep(5000);}catch(Exception ignore){}
        Scan.editMessage(idMessage, "messaggio da eliminare");
        try{
            ThreadParse.sleep(5000);}catch(Exception ignore){}
        Scan.deleteMessage(idMessage);
    }
}