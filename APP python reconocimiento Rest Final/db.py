import sqlite3
from os import path,getcwd

"""
Esta clase realiza la conexión hacia la base de datos y la interacción
De los datos en esta base de los mismos para lo cual se realizan
las importaciones de las librerías necesarias. 

"""


db = path.join(getcwd(), 'database.db')

class Database:
    
    #Metodo que genera la coneccion hacia la base de datos
    def __init__(self):
        self.connection = sqlite3.connect(db,check_same_thread=False)

 

    def query(self, q, arg=()):
        cursor = self.connection.cursor()

        cursor.execute(q, arg)
        results = cursor.fetchall()
        cursor.close()

        return results


    def insert(self, q, arg=()):
        cursor = self.connection.cursor()

        cursor.execute(q, arg)

        self.connection.commit()
        result = cursor.lastrowid
        cursor.close()
        return result



    def select(self, q, arg=()):
        cursor = self.connection.cursor()

        return cursor.execute(q, arg)


    def delete(self, q, arg=()):
        cursor = self.connection.cursor()
        result = cursor.execute(q, arg)
        self.connection.commit()
        return result