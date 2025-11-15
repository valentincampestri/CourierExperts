package com.courierexperts.demo.ui.signup;

import android.os.Parcel;
import android.os.Parcelable;

public class SignUpData implements Parcelable {
    private final String nombre;
    private final String apellido;
    private final String dni;
    private final String cuil;

    public SignUpData(String nombre, String apellido, String dni, String cuil) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.dni = dni;
        this.cuil = cuil;
    }

    protected SignUpData(Parcel in) {
        nombre = in.readString();
        apellido = in.readString();
        dni = in.readString();
        cuil = in.readString();
    }

    public static final Creator<SignUpData> CREATOR = new Creator<SignUpData>() {
        @Override
        public SignUpData createFromParcel(Parcel in) {
            return new SignUpData(in);
        }

        @Override
        public SignUpData[] newArray(int size) {
            return new SignUpData[size];
        }
    };

    public String getNombre() {
        return nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public String getDni() {
        return dni;
    }

    public String getCuil() {
        return cuil;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(nombre);
        dest.writeString(apellido);
        dest.writeString(dni);
        dest.writeString(cuil);
    }
}
