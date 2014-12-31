/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mokko.comm;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author psyriccio
 */
public class SessionManager {

    private ConcurrentHashMap<String, ConcurrentHashMap<String, Object>> sessions;
    private ConcurrentHashMap<String, Long> lastActivityTimeMap;
    private final long sessionLifetime;
    
    public void checkSession(String sessionId) {
        if(sessions.containsKey(sessionId)) {
            long now = new Date().getTime();
            if(lastActivityTimeMap.containsKey(sessionId)) {
                long lastActivity = lastActivityTimeMap.get(sessionId);
                if( (now - lastActivity) > sessionLifetime) {
                    deleteSession(sessionId);
                    throw new RuntimeException("Session timed out");
                } else {
                    lastActivityTimeMap.put(sessionId, now);
                }
            } else {
                lastActivityTimeMap.put(sessionId, now);
            }
        } else {
            throw new RuntimeException("Session not exists");
        }
    }
    
    public String createSession(String name) {
        String sessionId = UUID.randomUUID().toString();
        ConcurrentHashMap<String, Object> sessionProps = new ConcurrentHashMap<>();
        sessionProps.put("name", name);
        sessions.put(sessionId, sessionProps);
        checkSession(sessionId);
        return sessionId;
    }
    
    public ConcurrentHashMap<String, Object> findSession(String sessionId) {
        checkSession(sessionId);
        return sessions.get(sessionId);
    }
    
    public void deleteSession(String sessionId) {
        sessions.remove(sessionId);
        lastActivityTimeMap.remove(sessionId);
    }
    
    public SessionManager(long sessionLifetime) {
        this.sessionLifetime = sessionLifetime;
        this.sessions = new ConcurrentHashMap<>();
        this.lastActivityTimeMap = new ConcurrentHashMap<>();
    }
    
}
