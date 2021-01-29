package com.andrea.materialfilediff;

/** POJO Class
 * Rappresenta le proprietà di un file di interesse a questa applicazione
 *
 *
 */
public class FileRepresentation {
    public String nome;
    public String hash;
    public int size;

    public FileRepresentation(String nome, String hash, int size) {
        this.nome = nome;
        this.hash = hash;
        this.size = size;
    }

    public FileRepresentation(String nome, String hash) {
        this.nome = nome;
        this.hash = hash;
    }
    public FileRepresentation() {
    }
}
