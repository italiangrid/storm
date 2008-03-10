/* 
 * The following snippet of code is taken from the ``various.i`` library file
 * in SWIG 1.3.24; the same library file from 1.3.19 has different names;
 * at some point in time, they should be removed from here...
 *
 * UPDATE 16.05.2007: The ``various.i`` code from 1.3.24 for the string_array is bogus!!!!
 * The following code is taken from the SWIG 1.3.31, this solve a big problem:
 * NEW: new char [strlen(c_string)+1] <-> OLD: char [strlen((c_string)+1)]!!!!
 * that cause the CRASH of the JVM in case of string[] not null.
 * @author: Luca M.
 */

/* 
 * char **STRING_ARRAY typemaps. 
 * These typemaps are for C String arrays which are NULL terminated.
 *   char *values[] = { "one", "two", "three", NULL }; // note NULL
 * char ** is mapped to a Java String[].
 *
 * Example usage wrapping:
 *   %apply char **STRING_ARRAY { char **input };
 *   char ** foo(char **input);
 *  
 * Java usage:
 *   String numbers[] = { "one", "two", "three" };
 *   String[] ret = modulename.foo( numbers };
 */
 
%typemap(jni) char **STRING_ARRAY "jobjectArray"
%typemap(jtype) char **STRING_ARRAY "String[]"
%typemap(jstype) char **STRING_ARRAY "String[]"
%typemap(in) char **STRING_ARRAY (jint size) {
    int i = 0;
    size = JCALL1(GetArrayLength, jenv, $input);
#ifdef __cplusplus
    $1 = new char*[size+1];
#else
    $1 = (char **)calloc(size+1, sizeof(char *));
#endif
    for (i = 0; i<size; i++) {
        jstring j_string = (jstring)JCALL2(GetObjectArrayElement, jenv, $input, i);
        const char *c_string = JCALL2(GetStringUTFChars, jenv, j_string, 0);
#ifdef __cplusplus
        $1[i] = new char [strlen(c_string)+1];
#else
        $1[i] = (char *)calloc(strlen(c_string)+1, sizeof(const char *));
#endif
        strcpy($1[i], c_string);
        JCALL2(ReleaseStringUTFChars, jenv, j_string, c_string);
        JCALL1(DeleteLocalRef, jenv, j_string);
    }
    $1[i] = 0;
}

%typemap(freearg) char **STRING_ARRAY {
    int i;
    for (i=0; i<size$argnum-1; i++)
#ifdef __cplusplus
      delete[] $1[i];
    delete[] $1;
#else
      free($1[i]);
    free($1);
#endif
}

%typemap(out) char **STRING_ARRAY (char *s) {
    int i;
    int len=0;
    jstring temp_string;
    const jclass clazz = JCALL1(FindClass, jenv, "java/lang/String");

    while ($1[len]) len++;
    jresult = JCALL3(NewObjectArray, jenv, len, clazz, NULL);
 
   /* exception checking omitted */

    for (i=0; i<len; i++) {
      temp_string = JCALL1(NewStringUTF, jenv, *result++);
      JCALL3(SetObjectArrayElement, jenv, jresult, i, temp_string);
      JCALL1(DeleteLocalRef, jenv, temp_string);
    }
}

%typemap(javain) char **STRING_ARRAY "$javainput"
%typemap(javaout) char **STRING_ARRAY  {
    return $jnicall;
  }
  