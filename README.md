# boot-config-loader
### Just doing something that everyone agrees is a bad idea.

But seriously, I'm jsut kind of messing around with different 
ideas here to get a better understanding of how spring boot configuration 
is loaded and what is possible to do with annotations.

### Conclusion: Yup. It was a bad idea. 

The intent was to figure out how to use unique spring boot configuration for each '*.war' deployed to Tomcat, and have those work for logging configuration as well. Take a look at "this bootiful war deployment of mine" to see how it should actually be done.

    https://github.com/Pytry/bootiful-war-deployment
    

