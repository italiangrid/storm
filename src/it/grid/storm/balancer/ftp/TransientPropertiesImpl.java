/*
 *
 *  Copyright (c) Istituto Nazionale di Fisica Nucleare (INFN). 2006-2010.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

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

