# Nebulabrot reloaded

Second attempt at computing stupidly big Mandelbrot fractale with nebula rendering.

## Why?

To see the progress made over the last 6 years, mainly.

## Steps

Start with git instead of SVN

Start with the SCM as the first step of the project instead of adding it later

Use Gradle instead of Eclipse

Use a build management system from the beginning

Open the project in IJ only after it's been fully initialized

Update all the dependencies

Start writing tests instead of starting writing implementations.

Abstract more elements behind interfaces.

Much faster to develop, the time spent on tests is also much longer.

Refactoring immediately instead of later.

Reaching a working prototype is soooooo much faster :D, even with distractions

Testing coordinates conversion before using them is much more efficient.

Coordinates conversion are *not that easy*.

Writing smaller classes, splitting the code regularly as classes have too much logic in them.

Multithreading GUI update is still not trivial.

Repeated mistakes:
 
* making a mistake again for rectangle overlaping that lead me to the same stackoverflow explanation as last time about how to do it properly.

* messing up with viewpoints with then leads to slightly-off-but-still-usable component behaviour.


