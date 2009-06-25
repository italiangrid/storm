package it.grid.storm.balancer.ftp;

/**
 * This class is part of the StoRM project.
 * Copyright (c) 2008 INFN-CNAF.
 * <p>
 * 
 * This class implements a concrete strategy to provide a Random policy.
 * The next Element is chosen in a random way. 
 *
 * Authors:
 * 		@author lucamag
 *
 * @param <T>
 */

public class TransientPropertiesImpl<T> implements TransientProperties<T> {

 private final T value;
 private final long expirationTime;

 /**
  * Constructor.
  * 
  * @param value - Any objet.
  * @param ttl - the time to live in milliseconds.
  */
 public TransientPropertiesImpl( T value, long ttl ) {
   this.value = value;
   this.expirationTime = (ttl == Long.MAX_VALUE) ? Long.MAX_VALUE : (System.currentTimeMillis() + ttl);
 }

 public T getValue() {
   if (System.currentTimeMillis() < expirationTime) {
     // The value is still valid.
     return value;
   }
   
   // The value has expired.
   return null;
 }

 public long getExpirationTime() {
   return expirationTime;
 }
 
 public String toString() {
   StringBuffer sb = new StringBuffer();
   sb.append( "[" ).append( value ).append( "]" );
   long timeLeft = expirationTime - System.currentTimeMillis();
   if (timeLeft < 0) {
     sb.append( " - Value has expired." );
   } else {
     sb.append( " - Value expires in " ).append(  timeLeft ).append(  " milliseconds." );
   }
   return sb.toString();
 }
}

