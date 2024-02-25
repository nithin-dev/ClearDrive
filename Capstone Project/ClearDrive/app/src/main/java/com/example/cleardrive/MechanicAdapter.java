package com.example.cleardrive;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class MechanicAdapter extends RecyclerView.Adapter<MechanicAdapter.ViewHolder> {
    private static List<Mechanic> mechanics;
    private Context context;
    private static OnItemClickListener listener;

    public MechanicAdapter(Context context, List<Mechanic> mechanics) {
        this.context = context;
        this.mechanics = mechanics;
    }
    public void setMechanics(List<Mechanic> mechanics) {
        this.mechanics = mechanics;
        notifyDataSetChanged();
    }
    public interface OnItemClickListener {
        void onItemClick(Mechanic mechanic);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_mechanic, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Mechanic mechanic = mechanics.get(position);
        holder.imageView.setImageResource(mechanic.getImageResource());
        holder.textViewName.setText(mechanic.getName());
        holder.textViewAddress.setText(mechanic.getAddress());
        holder.mobile.setText(mechanic.getMobile());
        holder.email.setText(mechanic.getEmail());

        holder.textViewPrice.setText(String.valueOf(mechanic.getPrice()));
    }

    @Override
    public int getItemCount() {
        return mechanics.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textViewName;
        TextView textViewAddress,mobile,email;
        TextView textViewPrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imgMechanic);
            textViewName = itemView.findViewById(R.id.tvName);
            textViewAddress = itemView.findViewById(R.id.tvAddress);
            textViewPrice = itemView.findViewById(R.id.tvPrice);
            mobile = itemView.findViewById(R.id.tvMobile);
            email = itemView.findViewById(R.id.tvEmail);

            itemView.setOnClickListener(v -> {
                if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    listener.onItemClick(mechanics.get(getAdapterPosition()));
                }
            });

        }
    }
}
