package ec.edu.uce.controlador;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import ec.edu.uce.final_2h_g06.R;
import ec.edu.uce.modelo.entidades.Vehiculo;

public class ListaVehiculosAdapter  extends RecyclerView.Adapter<ListaVehiculosAdapter.VehiculosViewHolder>{

    ArrayList<Vehiculo> listaVehiculo;

    public ListaVehiculosAdapter(ArrayList<Vehiculo> listaVehiculo) {
        this.listaVehiculo = listaVehiculo;
    }

    @NonNull
    @Override
    public VehiculosViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_lista_vehiculos,null,false);
        return new VehiculosViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VehiculosViewHolder vvHolder, int i) {

            vvHolder.placaLVA.setText("Placa: " + listaVehiculo.get(i).getPlaca());
            vvHolder.marcaLVA.setText("Marca: " + listaVehiculo.get(i).getMarca());
            String fecha = new SimpleDateFormat("yyyy-MM-dd").format(listaVehiculo.get(i).getFechaFabricacion());
            vvHolder.fechaLVA.setText("Fecha de fabricacion: " + fecha);
            vvHolder.costoLVA.setText("Costo: " + listaVehiculo.get(i).getCosto().toString());
            if (listaVehiculo.get(i).getMatriculado() == true) {
                vvHolder.matriculadoLVA.setText("Matriculado: " + "SI");
            } else {
                vvHolder.matriculadoLVA.setText("Matriculado: " + "NO");
            }
            vvHolder.colorLVA.setText("Color: " + listaVehiculo.get(i).getColor());
            vvHolder.tipoLVA.setText("Tipo: " + listaVehiculo.get(i).getTipo());
            vvHolder.imagenLVA.setImageBitmap(listaVehiculo.get(i).getFotoAux());

    }

    @Override
    public int getItemCount() {
        return listaVehiculo.size();
    }

    public class VehiculosViewHolder extends RecyclerView.ViewHolder {
        TextView placaLVA;
        TextView marcaLVA;
        TextView fechaLVA;
        TextView costoLVA;
        TextView matriculadoLVA;
        TextView colorLVA;
        ImageView imagenLVA;
        TextView tipoLVA;

        public VehiculosViewHolder(@NonNull View itemView) {
            super(itemView);
            placaLVA = itemView.findViewById(R.id.placaLV);
            marcaLVA = itemView.findViewById(R.id.marcaLV);
            fechaLVA = itemView.findViewById(R.id.fechaLV);
            costoLVA = itemView.findViewById(R.id.costoLV);
            matriculadoLVA = itemView.findViewById(R.id.matriculadoLV);
            colorLVA = itemView.findViewById(R.id.colorLV);
            imagenLVA = itemView.findViewById(R.id.imagenLV);
            tipoLVA = itemView.findViewById(R.id.tipoLV);
        }
    }
}
