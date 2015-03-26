-- phpMyAdmin SQL Dump
-- version 4.2.11
-- http://www.phpmyadmin.net
--
-- Servidor: 127.0.0.1
-- Tiempo de generación: 26-03-2015 a las 01:02:16
-- Versión del servidor: 5.6.21
-- Versión de PHP: 5.6.3

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Base de datos: `nfc_database`
--

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `log`
--

CREATE TABLE IF NOT EXISTS `log` (
  `relacion` varchar(100) NOT NULL,
  `objetoPadre` varchar(100) NOT NULL,
  `objeto` varchar(100) NOT NULL,
  `interaccion` varchar(100) NOT NULL,
  `tiempo` varchar(100) NOT NULL,
  `sincro` int(11) NOT NULL DEFAULT '0'
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Volcado de datos para la tabla `log`
--

INSERT INTO `log` (`relacion`, `objetoPadre`, `objeto`, `interaccion`, `tiempo`, `sincro`) VALUES
('Esta en', 'libro3', 'libro3', 'Espacial', '1427327568', 1);

--
-- Disparadores `log`
--
DELIMITER //
CREATE TRIGGER `test_delete` AFTER DELETE ON `log`
 FOR EACH ROW BEGIN
    DELETE FROM objetos WHERE NOT EXISTS (SELECT objeto
    FROM log WHERE objeto=objetos.objeto OR objetoPadre=objetos.objeto);
    
    DELETE FROM relacion WHERE NOT EXISTS (SELECT relacion 
    FROM log WHERE relacion=relacion.relacion);
    
    DELETE FROM tipo_interacion WHERE NOT EXISTS (SELECT interaccion 
    FROM log WHERE interaccion=tipo_interacion.interacion);
                                                  
    DELETE FROM rcruzadas WHERE NOT EXISTS (SELECT relacion,objetoPadre
    FROM log WHERE relacion=rcruzadas.relacion AND    	objetoPadre=rcruzadas.objeto);

END
//
DELIMITER ;
DELIMITER //
CREATE TRIGGER `test_insercion` BEFORE INSERT ON `log`
 FOR EACH ROW BEGIN
    INSERT INTO objetos (objeto) SELECT  NEW.objeto
    FROM dual 
    WHERE NOT EXISTS (SELECT objeto FROM objetos WHERE objeto =   	   NEW.objeto)LIMIT 1;    
   
   INSERT INTO objetos (objeto) SELECT  NEW.objetoPadre
    FROM dual 
    WHERE NOT EXISTS (SELECT objeto FROM objetos WHERE objeto =   	   NEW.objetoPadre)LIMIT 1;    
    
     INSERT INTO relacion (relacion) SELECT  NEW.relacion
    FROM dual 
    WHERE NOT EXISTS (SELECT relacion FROM relacion WHERE relacion =   	   NEW.relacion)LIMIT 1;    
    
    INSERT INTO tipo_interacion (interacion) SELECT  NEW.interaccion
    FROM dual
    WHERE NOT EXISTS (SELECT interacion FROM tipo_interacion WHERE interacion = NEW.interaccion)LIMIT 1;
    
    INSERT INTO rcruzadas (relacion,objeto,interacion) SELECT NEW.relacion,NEW.objeto,NEW.interaccion FROM dual WHERE NOT EXISTS 
(SELECT relacion,objeto,interacion FROM rcruzadas WHERE relacion=NEW.relacion AND objeto=NEW.objeto AND interacion=NEW.interaccion);
    
  END
//
DELIMITER ;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `objetos`
--

CREATE TABLE IF NOT EXISTS `objetos` (
  `objeto` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Volcado de datos para la tabla `objetos`
--

INSERT INTO `objetos` (`objeto`) VALUES
('libro3');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `rcruzadas`
--

CREATE TABLE IF NOT EXISTS `rcruzadas` (
  `relacion` varchar(100) NOT NULL,
  `objeto` varchar(100) NOT NULL,
  `interacion` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Volcado de datos para la tabla `rcruzadas`
--

INSERT INTO `rcruzadas` (`relacion`, `objeto`, `interacion`) VALUES
('Esta en', 'libro3', 'Espacial');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `relacion`
--

CREATE TABLE IF NOT EXISTS `relacion` (
  `relacion` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Volcado de datos para la tabla `relacion`
--

INSERT INTO `relacion` (`relacion`) VALUES
('Esta en');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `tipo_interacion`
--

CREATE TABLE IF NOT EXISTS `tipo_interacion` (
  `interacion` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Volcado de datos para la tabla `tipo_interacion`
--

INSERT INTO `tipo_interacion` (`interacion`) VALUES
('Espacial');

--
-- Índices para tablas volcadas
--

--
-- Indices de la tabla `log`
--
ALTER TABLE `log`
 ADD PRIMARY KEY (`tiempo`);

--
-- Indices de la tabla `objetos`
--
ALTER TABLE `objetos`
 ADD PRIMARY KEY (`objeto`);

--
-- Indices de la tabla `rcruzadas`
--
ALTER TABLE `rcruzadas`
 ADD PRIMARY KEY (`relacion`,`objeto`,`interacion`);

--
-- Indices de la tabla `relacion`
--
ALTER TABLE `relacion`
 ADD PRIMARY KEY (`relacion`);

--
-- Indices de la tabla `tipo_interacion`
--
ALTER TABLE `tipo_interacion`
 ADD PRIMARY KEY (`interacion`);

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
