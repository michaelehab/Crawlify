package com.example.crawlify.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.zip.GZIPOutputStream;

@Document(collection = "page")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Page {
    @Id
    private String id;
    private String url;
    private String title;
    private String html;

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

        return Base64.getEncoder().encodeToString(compressedHtml);
    }
}