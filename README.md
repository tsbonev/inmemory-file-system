# inmemory-file-system

Implements a file system using a tree method in-memory

The deletions only separate files from the root, the garbage collection is left to Java
No indexing has been done to make searching faster, all searches start from root
There is no validation added for a name, except the inability to use "/" withing one
