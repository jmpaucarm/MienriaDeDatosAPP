from flask import Flask,json,Response,request
from werkzeug.utils import secure_filename
from os import path,getcwd
from db import Database
from face import Face
import time


app=Flask(__name__)
app.config['file_allowed']=['image/png','image/jpeg']
app.config['storage']=path.join(getcwd(),'storage')
app.db=Database()
app.face=Face(app)




#*************************************************Manejo de errores y stagus code***************************************************
def  succes_handle(output, status=200,mimetype='application/json'):
    return Response(output,status=status,mimetype=mimetype)

def  error_handle(error_message, status=500,mimetype='application/json'):
    return Response(json.dumps({"error":{"message":error_message}}),status=status,mimetype=mimetype)
#***********************************************************************************************************************************

#**************************************************************Obtener usuario por id*********************************************************************
def get_user_by_id(user_id):
    user={}
    results = app.db.select('SELECT users.id, users.name, users.created, faces.id, faces.user_id, faces.filename,faces.created FROM users LEFT JOIN faces ON faces.user_id = users.id WHERE users.id = ?',[user_id])
    index = 0

    for row in results:
        print(row)
        face = {
            "id": row[3],
            "user_id": row[4],
            "filename": row[5],
            "created": row[6],
        }
        if index == 0:
            user = {
                "id": row[0],
                "name": row[1],
                "created": row[2],
                "faces": [],
            }
        if row[3]:
            user["faces"].append(face)
        index = index + 1

    if 'id' in user:
        return user
    else:
        return None

#**************************************************************Eliminar usuario por id*********************************************************************
def delete_user_by_id(user_id):
    app.db.delete('DELETE FROM users WHERE users.id = ? ',[user_id])
    # Tabien eliminar todas las Faces con el id del usuario
    app.db.delete('DELETE FROM FACES WHERE faces.user_id = ?',[user_id])


@app.route('/',methods=['GET'])
def homepage():
    print('bienvenido a la homepage')
    output = json.dumps({"api": '1.0'})
    return succes_handle(output)
    #return error_handle("error hp")

@app.route('/api/train',methods=['POST'])
def train():
    output=json.dumps({"succes" :True})
    
    if 'file' not in request.files:
        print("foto de laj caraj ej requerida")
        return error_handle("foto de laj caraj ej requerida")
    else:
        print("File request ",request.files)
        file=request.files['file']
        
        #Validacion del tipo de archivo que se va a enviar  solo aceptara lo definido en app.config

        if file.mimetype not in app.config['file_allowed']:
            print("Extencion de archivo no es permitida")
            return error_handle("Extencion de archivo no es permitida envie un .jpg o un .png")
        else:
            #Obtener el nombre de la imagen e data
            
            name=request.form['name']#Obtiene el nombre de la imagen desde el request que tenga el atributo name
            print("La informacion de esa imagen es: ",name)

            print("Archivo permitido se guardara en ",app.config['storage'])
            #------------>Se permitira a la imagen almacenarse en la base de datos 
            filename = secure_filename(file.filename)
            #Guarda el archivo en la ruta seleccionada
            trained_storage=path.join(app.config['storage'],'trained')
            file.save(path.join(trained_storage,filename))
            print("el nombre del nuevo archivo es ",filename)
            #----->Se permitira a la imagen almacenarse en la base de datos 

            # Guardar  al sql
            created = int(time.time())
            user_id = app.db.insert('INSERT INTO users(name, created) values(?,?)', [name, created])

            if user_id:
                print("Datos del usuario guardados",name,user_id)
                #Usuario a sido guardado con user_id y ahora necesitamos guardar la tabla faces tambien
                face_id=app.db.insert('INSERT INTO faces(user_id, filename, created) values (?,?,?)',[user_id,filename,created])
                if face_id:
                    print("BIEN LA IMAGEN A SIDO GUARDADA")
                    face_data={"id":face_id,"filename":filename,"created":created}
                    return_output = json.dumps({"id" : user_id, "name" : name, "face": [face_data]})
                    return succes_handle(return_output)
                else:
                    print("Error Guardando La Imagen")
                    return error_handle("Error Guardando La Imagen")
            else:
                print("Algo malo Sucedio")
                return error_handle("Error insertando un nuevo usuario")
              
            return succes_handle(output)
        print("Request contiene una imagen")

    return succes_handle(output)

#route para el perfil del usuario
@app.route('/api/users/<user_id>',methods=['GET','DELETE'])
def user_profile(user_id):
    if request.method == 'GET':
        user = get_user_by_id(user_id)
        if user:
            return succes_handle(json.dumps(user),200)
        else:
            return error_handle("Usuario no encontrado",404)
    if request.method == 'DELETE':
        delete_user_by_id(user_id)
        return succes_handle(json.dumps({"deleted ": True}))

#Router para reconocer una nueva cara

@app.route('/api/recognize',methods=['POST'])
def recognize():
    if 'file' not in request.files:
        return error_handle("Image is required")
    else:
         file = request.files['file']
        # file extension valiate
         if file.mimetype not in app.config['file_allowed']:
            return error_handle("Extencion de archivo no permitida")
         else:
            filename = secure_filename(file.filename)
            unknown_storage = path.join(app.config["storage"], 'unknown')
            file_path = path.join(unknown_storage, filename)
            file.save(file_path)

            user_id = app.face.recognize(filename)
            if user_id:
                user = get_user_by_id(user_id)
                message = {"message": "Hey we found {0} matched with your face image".format(user["name"]), "user": user}
                return  succes_handle(json.dumps(message))
            else:
                 return error_handle("Perdon no podemos encontrar ninguna persona que coincida con tu imagen trate otra imagen")

    return succes_handle  (json.dumps({"El nombre el archivo a comprar es :":filename}))
            #user_id = app.face.recognize(filename)
            #if user_id:
             #   user = get_user_by_id(user_id)
              #  message = {"message": "Hey we found {0} matched with your face image".format(user["name"]),
               #            "user": user}
                #return success_handle(json.dumps(message))
            #else:

             #   return error_handle("Sorry we can not found any people matched with your face image, try another image")





app.run(port=8000)