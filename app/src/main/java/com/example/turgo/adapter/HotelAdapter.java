package com.example.turgo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.turgo.R;
import com.example.turgo.fragments.FlightsFragment;
import com.example.turgo.fragments.VisitParkFragment;
import org.javatuples.Pair;
import java.util.List;


public class HotelAdapter extends RecyclerView.Adapter<HotelAdapter.ViewHolder> {
    private static final String TAG = "HotelAdapter";
    private Context context;
    private List<Pair<String, String>> hotels;
    public static String clickedHotel;

    public HotelAdapter(Context context, List<Pair<String, String>> hotels) {
        this.context = context;
        this.hotels = hotels;
    }

    @Override
    public int getItemCount() { return hotels.size(); }
    @NonNull
    @Override
    public HotelAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_hotel, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Pair<String, String> hotel = hotels.get(position);
        holder.bind(hotel);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvHotelName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvHotelName = itemView.findViewById(R.id.tvHotelName);
        }

        public void bind(Pair<String, String> hotel) {
            tvHotelName.setText(hotel.getValue0());
            tvHotelName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickedHotel = hotel.getValue1(); // HOTEL ID for amadeus
                    FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.flContainer, new FlightsFragment()).commit();
                }
            });
        }
        public void clear() {
            hotels.clear();
            notifyDataSetChanged();
        }

        public void addAll(List<Pair<String, String>> list) {
            hotels.addAll(list);
            notifyDataSetChanged();
        }
    }
}