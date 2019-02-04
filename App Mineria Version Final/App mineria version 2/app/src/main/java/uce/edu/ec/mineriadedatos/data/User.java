package uce.edu.ec.mineriadedatos.data;

import java.util.ArrayList;

public class User {
        private String cedula;
        private float created;
        ArrayList< Object > faces = new ArrayList < Object > ();
        private float id;
        private String name;
        private String password;
        // Getter Methods
        public String getCedula() {
            return cedula;
        }
        public float getCreated() {
            return created;
        }
        public float getId() {
            return id;
        }
        public String getName() {
            return name;
        }
        public String getPassword() {
            return password;
        }

        public void setCedula(String cedula) {
            this.cedula = cedula;
        }
        public void setCreated(float created) {
            this.created = created;
        }
        public void setId(float id) {
            this.id = id;
        }
        public void setName(String name) {
            this.name = name;
        }
        public void setPassword(String password) {
            this.password = password;
        }
    }
