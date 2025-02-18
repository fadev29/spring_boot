package com.example.todolist.config;

import com.example.todolist.service.UserService;
import com.example.todolist.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
// untuk autentikasi token
@Component
public class JwtRequestFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUtil jwtUtil;

    private final UserService userService;

    @Autowired
    public JwtRequestFilter(@Lazy UserService userService) {
        this.userService = userService;
    }
    // method doFilterInternal untuk menfilter setiap request yang masuk
    @Override
    protected  void  doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException{
        // ambil header nya di token
        String authorizationHeader = request.getHeader("Authorization");

        String username = null;
        String jwt = null;
        // ambil informasi token
        if(authorizationHeader != null && authorizationHeader.startsWith("Bearer ")){
            jwt = authorizationHeader.substring(7);// ngambil token setelah urutan ke tujuh
            username = jwtUtil.extractUsername(jwt);// ambil username daro token yang diekstrak
        }
        // falidasi user
        if(username != null && SecurityContextHolder.getContext().getAuthentication() == null){
            // nampung object yang isinya informasi username,password,role/otoritas
            UserDetails userDetails = this.userService.loadUserByUsername(username);
            // nampung object yang isi nya adalah user yang sudah validasi
            if(jwtUtil.validateToken(jwt,userDetails)){
                // nampung object yang isis nya informasi username,pass,role/otoritas
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
                // buat nambahin detail informasi dari request yang dikirim
                usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                // menetapkan user telah di terautentikasi dan terotorisasi
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
        }
        // buat ngejalani konfigurasi filter
        filterChain.doFilter(request, response);
    }
}
