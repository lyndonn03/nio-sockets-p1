## About the Project

This project is a simplified implementation of client-server connection using `Java NIO` API. The server uses a `Selector` in which it can manage multiple channels in one thread. This is my take on learning the history of network concurrency in server side. Also, the concept of the architecture of this application is similar to `Thread per Request` model but instead of calling it in that way, I like to call this model as `Channel per Request` since every request of the client, the server will allocate a memory for a `SocketChannel` instead of a thread. The connection will be then closed by the client and server once the client receives the response. To know more about `Thread per Request` model and its simplified implementation in Java, you can go to this [link](https://github.com/lyndonn03/socket-p2) to get started.

<br>

## Running the Project

The repository has two parts, the server and the client. These are two different applications, so to run these, you need to run the two applications individually. You must first run the server and run the client. You can use your favorite IDE or text editor to run these easily. Otherwise, you need to compile and run these applications using `Java CLI`. You can also run multiple client applications.

<br>

## About the Channel per Request Model

This model is similar to the concept of `Thread per Request` model in a way that it spawns something per request. Instead of threads, the server will instantiate a `SocketChannel`, then add it to the `Selector`. When the `SocketChannel` is registered in the `Selector`, the `Selector` now can notify us when the `SocketChannel` is ready for reading requests or writing responses. If you have multiple client request, then it will spawn multiple `SocketChannel` that the `Selector` will manage. Note that the `Selector` manages all of the registered channels in one thread. This is the power of Non-Blocking I/O API.

<br>





