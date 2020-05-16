package za.co.sendmedelivery.sendmeacab;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

public class ProfileFragment extends Fragment implements View.OnClickListener {

    LinearLayout passwordLayout, passwordTrigger;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        Button btnChangePassword = (Button) v.findViewById(R.id.btnChangePassword);
        btnChangePassword.setOnClickListener(this);
        passwordTrigger = (LinearLayout) v.findViewById(R.id.passwordTriggerLayout);
        passwordLayout = (LinearLayout) v.findViewById(R.id.passwordLayout);

        Button btnSaveChanges = (Button) v.findViewById(R.id.btnSaveChanges);
        btnSaveChanges.setOnClickListener(this);

        return v;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnChangePassword:
                    passwordTrigger.setVisibility(v.GONE);
                    passwordLayout.setVisibility(v.VISIBLE);
                break;

            case R.id.btnSaveChanges:
                Toast.makeText(getActivity(), "Your changes are saved.", Toast.LENGTH_LONG).show();
                break;
        }
    }
}
