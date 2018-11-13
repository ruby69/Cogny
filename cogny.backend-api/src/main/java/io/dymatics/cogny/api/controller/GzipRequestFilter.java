package io.dymatics.cogny.api.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ReadListener;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GzipRequestFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        String encoding = req.getHeader("Content-Encoding");
        if (encoding != null && encoding.toLowerCase().contains("gzip")) {
            request = new GZIPServletRequestWrapper(req);
        }
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {}

    private class GZIPServletRequestWrapper extends HttpServletRequestWrapper {
        public GZIPServletRequestWrapper(HttpServletRequest request) {
            super(request);
        }

        @Override
        public ServletInputStream getInputStream() throws IOException {
            return new GZIPServletInputStream(super.getInputStream());
        }

        @Override
        public BufferedReader getReader() throws IOException {
            return new BufferedReader(new InputStreamReader(new GZIPServletInputStream(super.getInputStream())));
        }
    }

    private class GZIPServletInputStream extends ServletInputStream {
        private InputStream input;

        public GZIPServletInputStream(InputStream input) throws IOException {
            this.input = new GZIPInputStream(input);
        }

        @Override
        public int read() throws IOException {
            return input.read();
        }

        @Override
        public boolean isFinished() {
            boolean finished = false;
            try {
                if (input.available() == 0) {
                    finished = true;
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return finished;
        }

        @Override
        public boolean isReady() {
            boolean ready = false;
            try {
                if (input.available() > 0) {
                    ready = true;
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return ready;
        }

        @Override
        public void setReadListener(ReadListener listener) {
        }
    }
}