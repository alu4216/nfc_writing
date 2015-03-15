package com.example.nfc_writing;


public class Titular //Class for data of settings
{
    private String titulo;
    private String subtitulo;
 
    public Titular(){
        titulo = " ";
        subtitulo = " ";
    }
 
    public String getTitulo(){
        return titulo;
    }
 
    public String getSubtitulo(){
        return subtitulo;
    }
    public void setTitulo(String tit, String sub)
    {
    	titulo = tit;
    	subtitulo = sub;
    }
}