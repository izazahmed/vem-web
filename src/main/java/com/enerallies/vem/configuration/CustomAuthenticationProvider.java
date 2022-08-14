package com.enerallies.vem.configuration;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.enerallies.vem.dao.LookUpDao;
import com.enerallies.vem.exceptions.VEMAppException;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {
	
	/* Get the logger object*/
	private static final Logger logger = Logger.getLogger(CustomAuthenticationProvider.class);
	
	@Autowired LookUpDao lookUpDao;
	
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String name = authentication.getName();
        String password = authentication.getCredentials().toString();
        try {
			JSONObject userObj = lookUpDao.loadRestUserByUsername(name);
			
			if (userObj.get("userName") != null && userObj.get("userName").equals(name) 
					&& userObj.get("password").equals(password)) {
				
	            List<GrantedAuthority> grantedAuths = new ArrayList<>();
	            grantedAuths.add(new SimpleGrantedAuthority("ROLE_APP"));
	            
	            return new UsernamePasswordAuthenticationToken(name, password, grantedAuths);
	            
	        } else {
	            //return null;
	        	throw new UsernameNotFoundException("Username or password incorrect!");
	        }
		} catch (VEMAppException e) {
			logger.error("error", e);
			return null;
		}
    }
 
    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}