 This extends Assignment 1 using persistent data structures and additional similarity metrics. It requires two programs.

For each of at least 10,000 businesses, create a file-based representation containing everything needed for your similarity metric. You can use Java serialization, with the file name the same as the business ID. (If any two businesses have the same name, you can discard one of them.)
Create a persistent block-based file-based B-Tree or Hash Table mapping the business name to its business ID file name.
Traverse this map to pre-categorize (and somehow store) records into 5 to 10 clusters using k-means, k-medoids, or a similar metric as discussed in class and outlined in the course notes.

