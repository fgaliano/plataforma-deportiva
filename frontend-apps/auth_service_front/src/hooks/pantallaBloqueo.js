// src/hooks/pantallaBloqueo.js
/*
Para no tener que copiar y pegar los estados useState de cargando y textoCargando en cada pantalla, 
podemos agrupar esa lógica en un "Custom Hook" (una función que empaqueta estados de React).
*/
import { useState } from 'react';

export function pantallaBloqueo() {
  const [cargando, setCargando] = useState(false);
  const [textoCargando, setTextoCargando] = useState('');

  const setBloqueoPantalla = (lgBloqueo, texto) => {
    setCargando(lgBloqueo);
    setTextoCargando(texto);
  };

  return {
    cargando,
    textoCargando,
    setBloqueoPantalla
  };
}