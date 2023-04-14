package com.example.crawlify.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.zip.GZIPOutputStream;

@Document(collection = "page")
public class Page {
    @Id
    private String id;
    private String url;
    private String title;
    private String html;

    public Page(String url, String title, String html) {
        this.url = url;
        this.title = title;
        this.html = html;
    }

    public String getCompactString() {
        byte[] compressedHtml = null;

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            try {
                GZIPOutputStream gzos = new GZIPOutputStream(baos);

                try {
                    gzos.write(this.html.getBytes());
                    gzos.finish();
                    compressedHtml = baos.toByteArray();
                } catch (Throwable var8) {
                    try {
                        gzos.close();
                    } catch (Throwable var7) {
                        var8.addSuppressed(var7);
                    }

                    throw var8;
                }

                gzos.close();
            } catch (Throwable var9) {
                try {
                    baos.close();
                } catch (Throwable var6) {
                    var9.addSuppressed(var6);
                }

                throw var9;
            }

            baos.close();
        } catch (IOException var10) {
            var10.printStackTrace();
        }

        String base64Html = Base64.getEncoder().encodeToString(compressedHtml);
        return base64Html;
    }

    public String getId() {
        return this.id;
    }

    public String getUrl() {
        return this.url;
    }

    public String getTitle() {
        return this.title;
    }

    public String getHtml() {
        return this.html;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public void setUrl(final String url) {
        this.url = url;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public void setHtml(final String html) {
        this.html = html;
    }

    public Page(final String id, final String url, final String title, final String html) {
        this.id = id;
        this.url = url;
        this.title = title;
        this.html = html;
    }

    public Page() {
    }
}