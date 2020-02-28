#  Chatting Room - Chicago  

## Introduction  

In this project, we are aiming to create a chatting room which contains several clients, at least one server and one database. We might do something else to make our chatting room more functional if time allows.
    
The first part should be the database. We decided to use the JDBC in java and the PostgreSQL commands to create the database. The database is used to store the usernames, passwords and the chatting logs.  
The second part should be the server. The server is a connection between clients and the database. The server also has the responsibility of transferring the data from the clients and the database.  
The third part should be clients. The clients are the parts that the users could see and use. It should have a registration and login part, a chatting part, a typing part, a friend list part, a group chatting part.  		
The last part, we could make the chatting room more attractive.  We hope to innovate some unique features to improve the chatting experience; for example, the user can know the message they sent, whether to be read or not. The user also could send and receive not only the text but also files, pictures, links etc. Moreover, the user can withdraw the message; After that, other users would not see the message any more. Besides, the chatting data can be kept on the database for a while or save to local.  
  
## Architecture  
  
1. The **database** to store user information and chat history.  
2. The **server** to handle communication with the clients and the database.  
3. The **client** to communicate with the server. The GUI is included in the client which is used to present a user-friendly form of navigation.  
  
##  Specification  
The client should be able to:
1. Users can create a new account with username and password in the client.  
2. Users can log in to account via their own username and password.  
3. Users can retrieve the password through personal information.  
4. Users can make a friend list.  
5. Users can know the list of other users who are currently online.  
6. Users can access their chatting history.  
7. The system should store encrypted user passwords.  
8. Initiate chat with any user who is online.  
9. Engage in multiple chats with different users.  
10. Allow group chats and inviting of other users to join a chat. One person leaving the group chat should end the entire chat.  
11. Allow a user to view previous chats of which they have been a part of, and searching for specific phrases or other search keys.  
12. Users can know if the message is read or not.  
13. Users can recall the messages. After that, the message will disappear. Other users cannot see any more.  
  
    

