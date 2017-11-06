package ui.access;

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
import android.widget.Toast;

import javax.inject.Inject;

import main.Main;
import model.ConnectionStatus;
import model.LoginStatus;
import network.ServerConnection;
import remote.aut.bme.hu.remote.R;

public class AccessFragment extends Fragment implements AccessScreen {

    @Inject
    AccessPresenter presenter;

    @Inject
    ServerConnection serverConnection;

    private EditText editTextAccountUsername;
    private EditText editTextAccountPassword;
    private Button buttonAccountConnect;

    private EditText editTextAnonymousId;
    private EditText editTextAnonymousPassword;
    private Button buttonAnonymousConnect;
    private Button buttonGenerateId;
    private Button buttonGeneratePassword;

    public static final String TITLE = "Access";

    public static AccessFragment newInstance() {
        return new AccessFragment();
    }

    public AccessFragment() {
        Main.injector.inject(this);
        presenter.setScreen(this);
        presenter.setServerConnection(serverConnection);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_access, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        editTextAccountUsername = (EditText) getView().findViewById(R.id.inputAccountUsername);
        editTextAccountPassword = (EditText) getView().findViewById(R.id.inputAccountPassword);
        buttonAccountConnect = (Button) getView().findViewById(R.id.buttonAccountConnect);

        editTextAnonymousId = (EditText) getView().findViewById(R.id.inputAnonymousId);
        editTextAnonymousPassword = (EditText) getView().findViewById(R.id.inputAnonymousPassword);
        buttonAnonymousConnect = (Button) getView().findViewById(R.id.buttonAnonymousConnect);
        buttonGenerateId = (Button) getView().findViewById(R.id.buttonGenerateId);
        buttonGeneratePassword = (Button) getView().findViewById(R.id.buttonGeneratePassword);

        buttonAccountConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                accountConnectAction();
            }
        });
        buttonAnonymousConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                anonymousConnectAction();
            }
        });
        buttonGenerateId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generateIdAction();
            }
        });
        buttonGeneratePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generatePasswordAction();
            }
        });

        editTextAccountUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                presenter.getModel().setAccountUsername(editTextAccountUsername.getText().toString());
            }
        });

        editTextAccountPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                presenter.getModel().setAccountPassword(editTextAccountPassword.getText().toString());
            }
        });

        editTextAnonymousId.setEnabled(false);
        editTextAnonymousPassword.setEnabled(false);

        presenter.generateAnonymousId();
        presenter.generateAnonymousPassword();
    }

    private void accountConnectAction() {
        LoginStatus loginStatus = presenter.getModel().getAccountLoginStatus();
        if (loginStatus == LoginStatus.DISCONNECTED) {
            presenter.connectAccount();
        } else if (loginStatus == LoginStatus.CONNECTED) {
            presenter.disconnectAccount();
        }
    }

    private void anonymousConnectAction() {
        ConnectionStatus connectionStatus = presenter.getModel().getAnonymousConnectionStatus();
        if (connectionStatus == ConnectionStatus.DISCONNECTED) {
            presenter.connectAnonymous();
        } else if (connectionStatus == ConnectionStatus.CONNECTED) {
            presenter.disconnectAnonymous();
        }
    }

    private void generateIdAction() {
        presenter.generateAnonymousId();
    }

    private void generatePasswordAction() {
        presenter.generateAnonymousPassword();
    }

    @Override
    public void updateAnonymousId() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                editTextAnonymousId.setText(presenter.getModel().getAnonymousId());
            }
        });

    }

    @Override
    public void updateAnonymousPassword() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                editTextAnonymousPassword.setText(presenter.getModel().getAnonymousPassword());
            }
        });
    }

    @Override
    public void updateAccountConnectionStatus() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LoginStatus loginStatus = presenter.getModel().getAccountLoginStatus();
                boolean editEnabled = loginStatus == LoginStatus.DISCONNECTED;
                boolean connectEnabled = loginStatus != LoginStatus.WAITING;
                editTextAccountUsername.setEnabled(editEnabled);
                editTextAccountPassword.setEnabled(editEnabled);
                buttonAccountConnect.setEnabled(connectEnabled);
                buttonAccountConnect.setText(loginStatus.getAction());
            }
        });
    }

    @Override
    public void updateAnonymousConnectionStatus() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ConnectionStatus connectionStatus = presenter.getModel().getAnonymousConnectionStatus();
                boolean generateEnabled = connectionStatus == ConnectionStatus.DISCONNECTED;
                boolean connectEnabled = connectionStatus != ConnectionStatus.WAITING;
                buttonGenerateId.setEnabled(generateEnabled);
                buttonGeneratePassword.setEnabled(generateEnabled);
                buttonAnonymousConnect.setEnabled(connectEnabled);
                buttonAnonymousConnect.setText(connectionStatus.getAction());
            }
        });
    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
    }

}
