package com.example.turgo.adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.turgo.MainActivity;
import com.example.turgo.R;
import com.example.turgo.fragments.VisitParkFragment;
import com.example.turgo.models.Park;

import java.util.List;

public class ParkAdapter extends RecyclerView.Adapter<ParkAdapter.ViewHolder> {
    public final String TAG = "ParkAdapter";
    private Context context;
    private List<Park> parks;

    public ParkAdapter(Context context, List<Park> parks) {
        this.context = context;
        this.parks = parks;
    }
    @Override
    public int getItemCount() {
        return parks.size();
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_park, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Park park = parks.get(position);
        holder.bind(park);
    }



    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvParkName, tvPeopleOnPark, tvHours;
        private Button btnGoToPark;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvParkName = itemView.findViewById(R.id.tvParkName);
            tvPeopleOnPark = itemView.findViewById(R.id.tvPeopleOnPark);
            tvHours = itemView.findViewById(R.id.tvHours);
            btnGoToPark = (Button) itemView.findViewById(R.id.btnGoToPark);
        }
        public void bind(Park park) {
            tvParkName.setText(park.getParkName());
            tvPeopleOnPark.setText(String.valueOf(park.getNpeople())+" People here");
            if (park.getHours()==null) tvHours.setText("Hours not provided");
            else tvHours.setText(park.getHours());
            btnGoToPark.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // TODO: launch activity with direction api with map and overview polyline & asking how many are going for the update, button for GO, that changes to LEAVE after
                    FragmentManager fragmentManager = ((AppCompatActivity)context).getSupportFragmentManager();
                    Fragment fragment = new VisitParkFragment();
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("clickedPark", park);
                    fragmentManager.beginTransaction().replace(R.id.flContainer, new VisitParkFragment()).commit();
                    //
                }
            });
        }
        public void clear() {
            parks.clear();
            notifyDataSetChanged();
        }
        public void addAll(List<Park> list) {
            parks.addAll(list);
            notifyDataSetChanged();
        }
    }
}
