package ui.connection;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import javax.inject.Inject;

import application.Application;
import main.Main;
import model.ConnectionStatus;
import network.ServerConnection;
import remote.aut.bme.hu.remote.R;

public class ConnectionFragment extends Fragment implements ConnectionScreen {

    @Inject
    ConnectionPresenter presenter;

    @Inject
    Application application;

    @Inject
    ServerConnection serverConnection;

    private Button buttonConnect;
    private TextView textSentMessages;
    private TextView textReceivedMessages;
    private EditText editTextAddress;

    public static final String TITLE = "Connection";

    public static ConnectionFragment newInstance() {
        return new ConnectionFragment();
    }

    public ConnectionFragment() {
        Main.injector.inject(this);
        presenter.setScreen(this);
        presenter.setServerConnection(serverConnection);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_connection, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        editTextAddress = (EditText) getView().findViewById(R.id.inputAddress);
        buttonConnect = (Button) getView().findViewById(R.id.buttonConnect);
        textSentMessages = (TextView) getView().findViewById(R.id.textSentMessages);
        textReceivedMessages = (TextView) getView().findViewById(R.id.textReceivedMessages);
        buttonConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectAction();
            }
        });
        updateConnectionStatus();

        editTextAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                presenter.getModel().setAddress(editTextAddress.getText().toString());
            }
        });
    }

    protected void connectAction() {
        ConnectionStatus connectionStatus = presenter.getModel().getConnectionStatus();
        if (connectionStatus == ConnectionStatus.DISCONNECTED) {
            presenter.connect();
        } else if (connectionStatus == ConnectionStatus.CONNECTED) {
            presenter.disconnect();
        }
    }

    @Override
    public void updateConnectionStatus() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ConnectionStatus connectionStatus = presenter.getModel().getConnectionStatus();
                boolean connectEnabled = connectionStatus != ConnectionStatus.WAITING;
                boolean editEnabled = connectionStatus == ConnectionStatus.DISCONNECTED;

                buttonConnect.setEnabled(connectEnabled);
                buttonConnect.setText(connectionStatus.getAction());
                editTextAddress.setEnabled(editEnabled);
            }
        });
    }

    @Override
    public void updateSentMessages() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textSentMessages.setText("Sent messages: " + presenter.getModel().getSentMessages());
            }
        });
    }

    @Override
    public void updateReceivedMessages() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textReceivedMessages.setText("Received messages: " + presenter.getModel().getReceivedMessages());
            }
        });
    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
    }

}
