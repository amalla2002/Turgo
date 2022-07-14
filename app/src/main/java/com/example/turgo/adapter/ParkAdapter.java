package com.example.turgo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.turgo.R;
import com.example.turgo.fragments.VisitParkFragment;
import java.util.List;
import org.javatuples.Quintet;

public class ParkAdapter extends RecyclerView.Adapter<ParkAdapter.ViewHolder> {
    public final String TAG = "ParkAdapter";
    private Context context;
    private List<Quintet<String, String, Number, Number, Number> > parks;
    public static Quintet<String, String, Number, Number, Number> clickedPark;

    public ParkAdapter(Context context, List<Quintet<String, String, Number, Number, Number> > parks) {
        this.context = context;
        this.parks = parks;
    }

    @Override
    public int getItemCount() { return parks.size(); }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_park, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Quintet<String, String, Number, Number, Number> park = parks.get(position);
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
            btnGoToPark = itemView.findViewById(R.id.btnGoToPark);
        }

        public void bind(Quintet<String, String, Number, Number, Number> park) {
            tvParkName.setText(park.getValue0());
            tvPeopleOnPark.setText((park.getValue2()) + " People here");
            tvHours.setText(park.getValue1());
            btnGoToPark.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickedPark = park;
                    FragmentManager fragmentManager = ((AppCompatActivity)context).getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.flContainer, new VisitParkFragment()).commit();
                }
            });
        }
    }
}