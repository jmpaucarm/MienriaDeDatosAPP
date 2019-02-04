import face_recognition
from os import path

from os import path,listdir
"""
Esta clase esta encargada de manejar el algoritmo de 
reconocimiento facial por lo cual esta clase se encargara  
de tomar los datos del usuario y tratarlos para realizar 
la clasificacion para lo cual se realizo la importacion de las librerias necesarias
"""
class Face:
    #Metodo encargado de realizar el entrenamiento dependeiendo del usuario que ingrese al sistema
     def load_train_file_by_name(self,name_usuario):
         trained_storage = path.join(self.storage,'trained' )
         return path.join(trained_storage,name_usuario)
    
    #Metodo encargado de realizar la carga del usuario dependiendo del index obtenido
     def load_user_by_index_key(self, index_key = 0):
         key_str =str(index_key)
         if key_str in self.face_user_keys:
             return self.face_user_keys[key_str]
         return None
    
    #Metodo encargado de cargar la imagen que se desea predecir
     def load_unknown_file_byname (self,name):
         unknown_storage = path.join(self.storage,'unknown' )
         return path.join(unknown_storage,name)

    #metodo de inicializacion de la clase
     def __init__(self, app):
        self.storage = app.config["storage"]
        self.db=app.db
        self.faces = [] #alamecena todas las caras uen un arreglo
        self.known_encodding_faces=[]# data de las caras para el reconocimiento
        self.face_user_keys = {}
        #self.load_all()
    
    #metodo encargado de decodificar todas las imagenes del usuario en especifico  para realizar el entrenamiento del algoritmo
     def load_all(self,cedula_user):        
        
        results = self.db.select('SELECT faces.id, faces.cedula_user,faces.filename, faces.created FROM faces WHERE faces.cedula_user = ?',[cedula_user])

        for row in results:
            print(row)
            cedula_user = row[1]
            filename =row[2]
           
            face={
                "id" : row[0],
                "cedula_user" : cedula_user,
                "filename" : filename,
                "created" : row[3],
            }
            self.faces.append(face)
            
              
            path_imagen_usuario= path.join(self.load_train_file_by_name(cedula_user),filename)
            face_image = face_recognition.load_image_file(path_imagen_usuario)

           

            face_image_encodig = face_recognition.face_encodings(face_image)[0]
            index_key=len(self.known_encodding_faces)

            self.known_encodding_faces.append(face_image_encodig)
            index_key_string=str(index_key)
            self.face_user_keys['{0}'.format(index_key_string)] = cedula_user
        
        print(self.known_encodding_faces)
    
    #Metodo encargado de realizar el reconocimiento Facial una vez que el usuario realize el envio de la imagen
     def recognize(self,unknown_filename,cedula_usuario):
         
         
         self.load_all(cedula_usuario)
         unknown_image = face_recognition.load_image_file(self.load_unknown_file_byname(unknown_filename))
         unknown_encoding_image = face_recognition.face_encodings(unknown_image)[0]
         results = face_recognition.compare_faces(self.known_encodding_faces,unknown_encoding_image)

         print("resultados",results)
         index_key=0
         for matched in results:
             if matched:
                 
                 user_id = self.load_user_by_index_key(index_key)
                 self.faces.clear()
                 self.known_encodding_faces.clear()
                 self.face_user_keys.clear()
                 return user_id
             self.faces.clear()
             self.known_encodding_faces.clear()
             self.face_user_keys.clear()  
             index_key = index_key + 1
         return None
         





         
         

