package com.example.webscraping;

import com.google.gson.Gson;

import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Upload{
    public static void main(String[] args) throws SQLException{
         try(FileReader reader = new FileReader("collezione.json")){
             Carta[] collezione = new Gson().fromJson(reader, Carta[].class);
             for(Carta c : collezione){
                 System.out.println(c);
             }
             System.out.println(collezione.length);
             if(Scan.getString("vuoi che sovrascrivo la tabella? (s/n)").toLowerCase().equals("s")){
                 try(Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/starwarsunlimited", "root", "Minecraft35?")) {
                     try (Statement stmt = conn.createStatement()) {
                         stmt.executeUpdate("delete from carte");
                     } catch (SQLException e) {
                         System.out.println("statement");
                         throw e;
                     }
                 } catch(SQLException e){
                     System.out.println("connection");
                     throw e;
                 }
             }
             System.out.println("inizio l'upload");
             for(Carta c : collezione){
                 try(Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/starwarsunlimited", "root", "Minecraft35?")) {
                     try (Statement stmt = conn.createStatement()) {
                         if(stmt.executeUpdate(c.insertSql()) == 0){
                             System.out.print("non ");
                         }
                         System.out.println("ho fatto l'insert di " + c.espansione + "_" + c.numero + "(" + c.nome + " " + c.titolo.toUpperCase() + ")");
                     } catch (SQLException e) {
                         System.out.println("statement");
                         System.out.println(c.insertSql());
                         throw e;
                     }
                 } catch(SQLException e){
                     System.out.println("connection");
                     throw e;
                 }
             }
         }catch (IOException e){
             e.printStackTrace();
         }
    }
}