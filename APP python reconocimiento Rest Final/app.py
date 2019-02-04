from werkzeug.utils import secure_filename
from flask import Flask,json,Response,request
from os import path,getcwd,mkdir
from db import Database
from face import Face
from PIL  import Image
import time

"""
Esta clase se encarga de manejar el servicio REST
e integrar todas las demas clases que corresponden
a la logica de negocio.

"""



app=Flask(__name__)
app.config['file_allowed']=['image/png','image/jpeg']
app.config['storage']=path.join(getcwd(),'storage')
app.db=Database()
app.face=Face(app)




#Metodo que maneja los estados 200 de las peticiones http
def  succes_handle(output, status=200,mimetype='application/json'):
    return Response(output,status=status,mimetype=mimetype)

#Metodo que maneja todos los codigos de error que puedan surgir al momento de invocar a un servicio por defecto se define un error  500
def  error_handle(error_message, status=500,mimetype='application/json'):
    return Response(json.dumps({"error":{"message":error_message}}),status=status,mimetype=mimetype)


#Metod que devuleve todos los datos del usuario que esten registrados con una cedula ya establecida en el servidor
def get_user_by_id(user_id):
    user={}
    results = app.db.select('SELECT users.id, users.name, users.cedula, users.password ,users.created, faces.id, faces.cedula_user, faces.filename,faces.created FROM users LEFT JOIN faces ON faces.cedula_user = users.cedula WHERE users.cedula = ?',[user_id])#busqueda por cedula
    index = 0

    for row in results:
        print(row)
        face = {
            "id": row[5],
            "cedula_user": row[6],
            "filename": row[7],
            "created": row[8],
        }
        if index == 0:
            user = {
                "id": row[0],
                "name": row[1],
                "cedula": row[2],
                "password": row[3],
                "created": row[4],
                "faces": [],
            }
        if row[5]:
            user["faces"].append(face)
        index = index + 1

    if 'id' in user:
        return user
    else:
        return None

#Metodo que permite realizar la eliminacion de un usuario dado un numero de cedula
def delete_user_by_id(user_id):
    app.db.delete('DELETE FROM users WHERE users.cedula = ? ',[user_id])
    # Tabien elimina todos los registros de los rostros con el id del usuario
    app.db.delete('DELETE FROM FACES WHERE faces.cedula_user = ?',[user_id])

#Metodo de redireccion cuando se a enviado al root de los servicios
@app.route('/',methods=['GET'])
def homepage():
    print('Bienvenido a la api de Bio - Recognition')
    output = json.dumps({"api": '1.0'})
    return succes_handle(output)
    
#Metodo que llamara a la logica de negocio necesaria para realizar la codificacion y el entrenamiento del algoritmo
@app.route('/api/train',methods=['POST'])
def train():
    output=json.dumps({"succes" :True})
    
    if 'file' not in request.files:
        print("Foto del rostro requerida")
        return error_handle("La foto del rostro es requerida")
    else:
        print("File request ",request.files)
        file=request.files['file']
        
        #Validacion del tipo de archivo que se va a enviar  solo aceptara lo definido en app.config
        if file.mimetype not in app.config['file_allowed']:
            print("Extencion de archivo no es permitida")
            return error_handle("Extencion de archivo no es permitida envie un .jpg o un .png")
        else:
            #Obtener el nombre de la imagen y la  data correspondiente
            name=request.form['name']#Obtiene el nombre de la imagen desde el request que tenga el atributo name
            cedula=request.form['cedula']
            password=request.form['password']
            print("La informacion de esa imagen es: ",name)
            print("Archivo permitido se guardara en ",app.config['storage'])
            #------------>Se permitira a la imagen almacenarse en la base de datos 
            filename = secure_filename(file.filename)
            #Guarda el archivo en la ruta seleccionada
            
            trained_storage=path.join(app.config['storage'],'trained',cedula)
            if not path.exists(trained_storage):
                mkdir(trained_storage)
                file.save(path.join(trained_storage,filename))
                #Rotar la IMAGEN PARA EL CASO DE MI TELEFONO OJO
                path_imagen_rotada = path.join(trained_storage,filename) 
                imagen = Image.open(path_imagen_rotada)
                imagen.show()
                transposed  = imagen.transpose(Image.ROTATE_90)
                transposed.save(path_imagen_rotada)

                print("Directorio nuevo creado es " , trained_storage )
            else:
                file.save(path.join(trained_storage,filename))
                #Rotar la IMAGEN PARA EL CASO DE MI TELEFONO OJO
                path_imagen_rotada = path.join(trained_storage,filename) 
                imagen = Image.open(path_imagen_rotada)
                transposed  = imagen.transpose(Image.ROTATE_90)
                transposed.save(path_imagen_rotada)
                    
                print("Directorio existente la imagen se a guardado en " , trained_storage)

            print("El nombre del nuevo archivo es ",filename)
            #----->Se permitira a la imagen almacenarse en la base de datos 
            created = int(time.time())
            
            comprobacion_usuarios =app.db.query('SELECT name,cedula,password,created FROM users created WHERE cedula = ? ',[cedula])
            if len(comprobacion_usuarios)==0:
                user_id = app.db.insert('INSERT INTO users(name,cedula,password, created) values(?,?,?,?)', [name,cedula,password, created])
            else:
                user_id=app.db.insert('UPDATE users SET created =?',[created])
                print("Usuario ya existe en la base de datos actualizando fecha de modificacion")
           

            if user_id:
                print("Datos del usuario guardados",name,user_id,cedula,password)
                face_id=app.db.insert('INSERT INTO faces(user_id, cedula_user, filename, created) values (?,?,?,?)',[user_id,cedula,filename,created])
                if face_id:
                    print("CORRECTO LA IMAGEN A SIDO GUARDADA")
                    face_data={"id":face_id,"filename":filename,"created":created}
                    return_output = json.dumps({"id" : user_id, "name" : name,"cedula_user":cedula, "face": [face_data]})
                    return succes_handle(return_output)
                else:
                    print("Error Guardando La Imagen")
                    return error_handle("Error Guardando La Imagen")
            else:
                print("Algo malo Sucedio inconsistencias en la base de datos")
                return error_handle("Error insertando un nuevo usuario")
              
            return succes_handle(output)
        print("Request contiene una imagen")

    return succes_handle(output)

#Metodo que permite obtener un usuario usando su cedula o eliminarlo dependiendo de su metodo HHTP
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

#Metodo HTTP QUE INTERACTUA CON LA LOGICA DE NEGOCIO EL CUAL PERMITE RECONOCER EL ROSTRO DE UN USUARIO Y ENVIAR UNA RESPUESTA
@app.route('/api/recognize',methods=['POST'])
def recognize():
    if 'file' not in request.files:
        return error_handle("Se requiere la imagen para realizar el reconocimiento")
    else:

         file = request.files['file']
         cedula=request.form['cedula']
        # file extension valiate
         if file.mimetype not in app.config['file_allowed']:
            return error_handle("Extencion de archivo no permitida")
         else:
            filename = secure_filename(file.filename)
            unknown_storage = path.join(app.config["storage"], 'unknown')
            file_path = path.join(unknown_storage, filename)
            file.save(file_path)

            #Rotar la IMAGEN PARA EL CASO DE MI TELEFONO OJO
            imagen = Image.open(file_path)
            imagen.show()

            #transposed  = imagen.transpose(Image.ROTATE_90)
            #transposed.save(file_path)
            #transposed.show()




            user_id = app.face.recognize(filename,cedula)
            #print("**************************************************el id del usuario es",user_id)
            if user_id:
                user = get_user_by_id(user_id)
                message = {"message": "{0}     {1} ".format(user["name"],user["cedula"]), "user": user,"cedula":cedula}
                #message ={"message" :"usuario encontrado"}
                return  succes_handle(json.dumps(message))
            else:
                 return error_handle("Perdon no podemos encontrar ninguna persona que coincida con tu imagen trate otra imagen")

    return succes_handle  (json.dumps({"El nombre el archivo a comprar es :":filename}))

#Metodo que permite inicializar el servidor
app.run('192.168.1.13',port=8000)