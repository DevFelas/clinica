package com.IFPI.CLINICA.Util;

import com.IFPI.CLINICA.Model.Paciente;
import lombok.Getter;
import lombok.Setter;

public class DataShare {
    @Setter
    @Getter
    private static Paciente pacienteParaEditar;

    public static void limpar() {
        pacienteParaEditar = null;
    }
}