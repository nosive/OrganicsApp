package com.example.organicsapp

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxSize

import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController



// PRODUCTO

data class Producto(
    val nombre: String,
    val precio: Int,
    val emoji: String,
    val categoria: String
)



// LISTA DE PRODUCTOS


val productos = listOf(

    Producto(
        nombre = "Manzana orgánica",
        precio = 2000,
        emoji = "🍎",
        categoria = "Frutas y verduras"
    ),

    Producto(
        nombre = "Zanahoria orgánica",
        precio = 1500,
        emoji = "🥕",
        categoria = "Frutas y verduras"
    ),

    Producto(
        nombre = "Leche orgánica",
        precio = 2500,
        emoji = "🥛",
        categoria = "Lácteos"
    ),

    Producto(
        nombre = "Queso orgánico",
        precio = 3500,
        emoji = "🧀",
        categoria = "Lácteos"
    ),

    Producto(
        nombre = "Avena integral",
        precio = 2000,
        emoji = "🌾",
        categoria = "Cereales"
    ),

    Producto(
        nombre = "Granola orgánica",
        precio = 3000,
        emoji = "🌾",
        categoria = "Cereales"
    )
)



// ACTIVIDAD PRINCIPAL


class MainActivity : ComponentActivity() {

    private val pedirPermisoNotificaciones =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { permisoAceptado ->

            if (permisoAceptado) {
                mostrarNotificacion()
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        crearCanalNotificaciones()

        setContent {

            OrganicsApp(
                mostrarNotificacion = {
                    solicitarPermisoNotificaciones()
                }
            )
        }
    }



    // SOLICITAR PERMISO DE NOTIFICACIONES


    private fun solicitarPermisoNotificaciones() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            if (
                checkSelfPermission(
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {

                pedirPermisoNotificaciones.launch(
                    Manifest.permission.POST_NOTIFICATIONS
                )

            } else {

                mostrarNotificacion()
            }

        } else {

            mostrarNotificacion()
        }
    }



    // CREAR CANAL DE NOTIFICACIONES


    private fun crearCanalNotificaciones() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val canal = NotificationChannel(
                "ofertas",
                "Ofertas especiales",
                NotificationManager.IMPORTANCE_DEFAULT
            )

            canal.description =
                "Notificaciones sobre ofertas especiales de OrganicsApp"

            val administrador =
                getSystemService(
                    Context.NOTIFICATION_SERVICE
                ) as NotificationManager

            administrador.createNotificationChannel(canal)
        }
    }



    // MOSTRAR NOTIFICACIÓN


    private fun mostrarNotificacion() {

        val notificacion =
            NotificationCompat.Builder(
                this,
                "ofertas"
            )
                .setSmallIcon(
                    android.R.drawable.ic_dialog_info
                )
                .setContentTitle(
                    "OrganicsApp"
                )
                .setContentText(
                    "🍎 ¡Nueva oferta! Manzana orgánica a $1.500"
                )
                .setPriority(
                    NotificationCompat.PRIORITY_DEFAULT
                )
                .setAutoCancel(true)
                .build()


        if (
            Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            checkSelfPermission(
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {

            NotificationManagerCompat
                .from(this)
                .notify(
                    1,
                    notificacion
                )
        }
    }
}



// NAVEGACIÓN PRINCIPAL


@Composable
fun OrganicsApp(
    mostrarNotificacion: () -> Unit
) {

    val navController = rememberNavController()


    // Carrito general de la aplicación

    val carrito = remember {

        mutableStateListOf<Producto>()
    }


    NavHost(
        navController = navController,
        startDestination = "inicio"
    ) {



        // INICIO


        composable("inicio") {

            Inicio(

                abrirCatalogo = {

                    navController.navigate("catalogo")
                },

                abrirRegistro = {

                    navController.navigate("registro")
                },

                abrirPedido = {

                    navController.navigate("pedido")
                },

                abrirOfertas = {

                    navController.navigate("ofertas")
                },

                abrirCarrito = {

                    navController.navigate("carrito")
                }
            )
        }



        // CATÁLOGO


        composable("catalogo") {

            Catalogo(

                volverInicio = {

                    navController.popBackStack()
                },

                agregarAlCarrito = { producto ->

                    carrito.add(producto)
                },

                abrirCarrito = {

                    navController.navigate("carrito")
                }
            )
        }



        // CARRITO


        composable("carrito") {

            Carrito(

                productos = carrito,

                volverInicio = {

                    navController.popBackStack()
                },

                eliminarProducto = { producto ->

                    carrito.remove(producto)
                },

                realizarPedido = {

                    navController.navigate("pedido")
                }
            )
        }



        // REGISTRO


        composable("registro") {

            Registro(

                volverInicio = {

                    navController.popBackStack()
                }
            )
        }



        // PEDIDO


        composable("pedido") {

            Pedido(

                productos = carrito,

                volverInicio = {

                    navController.popBackStack()
                },

                pedidoRealizado = {

                    carrito.clear()
                }
            )
        }



        // OFERTAS


        composable("ofertas") {

            Ofertas(

                volverInicio = {

                    navController.popBackStack()
                },

                mostrarNotificacion = mostrarNotificacion
            )
        }
    }
}



// PANTALLA PRINCIPAL


@Composable
fun Inicio(

    abrirCatalogo: () -> Unit,

    abrirRegistro: () -> Unit,

    abrirPedido: () -> Unit,

    abrirOfertas: () -> Unit,

    abrirCarrito: () -> Unit
) {

    Column(

        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),

        horizontalAlignment = Alignment.CenterHorizontally,

        verticalArrangement = Arrangement.Center
    ) {

        Text(

            text = "OrganicsApp",

            style = MaterialTheme.typography.headlineLarge
        )


        Text(

            text = "Productos orgánicos para tu hogar",

            modifier = Modifier.padding(16.dp)
        )


        Button(

            onClick = abrirCatalogo

        ) {

            Text("Ver catálogo")
        }


        Button(

            onClick = abrirRegistro,

            modifier = Modifier.padding(top = 10.dp)

        ) {

            Text("Registrarse")
        }


        Button(

            onClick = abrirPedido,

            modifier = Modifier.padding(top = 10.dp)

        ) {

            Text("Realizar pedido")
        }


        Button(

            onClick = abrirOfertas,

            modifier = Modifier.padding(top = 10.dp)

        ) {

            Text("Ofertas especiales")
        }


        Button(

            onClick = abrirCarrito,

            modifier = Modifier.padding(top = 10.dp)

        ) {

            Text("🛒 Ver carrito")
        }
    }
}



// CATÁLOGO


@Composable
fun Catalogo(

    volverInicio: () -> Unit,

    agregarAlCarrito: (Producto) -> Unit,

    abrirCarrito: () -> Unit
) {

    var categoriaSeleccionada by remember {

        mutableStateOf(
            "Frutas y verduras"
        )
    }


    val productosFiltrados = productos.filter {

        it.categoria == categoriaSeleccionada
    }


    Column(

        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),

        horizontalAlignment = Alignment.CenterHorizontally
    ) {


        Text(

            text = "Catálogo de productos",

            style = MaterialTheme.typography.headlineMedium
        )


        Text(

            text = "Selecciona una categoría",

            modifier = Modifier.padding(
                top = 10.dp,
                bottom = 20.dp
            )
        )



        // CATEGORÍA FRUTAS


        Button(

            onClick = {

                categoriaSeleccionada =
                    "Frutas y verduras"
            }

        ) {

            Text("Frutas y verduras")
        }



        // CATEGORÍA LÁCTEOS


        Button(

            onClick = {

                categoriaSeleccionada =
                    "Lácteos"
            },

            modifier = Modifier.padding(
                top = 8.dp
            )

        ) {

            Text("Lácteos")
        }



        // CATEGORÍA CEREALES


        Button(

            onClick = {

                categoriaSeleccionada =
                    "Cereales"
            },

            modifier = Modifier.padding(
                top = 8.dp
            )

        ) {

            Text("Cereales")
        }


        Text(

            text = categoriaSeleccionada,

            style = MaterialTheme.typography.titleLarge,

            modifier = Modifier.padding(
                top = 30.dp,
                bottom = 15.dp
            )
        )



        // PRODUCTOS


        for (producto in productosFiltrados) {

            Text(

                text =
                    "${producto.emoji} ${producto.nombre}"
            )


            Text(

                text =
                    "$${producto.precio}",

                modifier = Modifier.padding(
                    top = 5.dp
                )
            )


            Button(

                onClick = {

                    agregarAlCarrito(producto)
                },

                modifier = Modifier.padding(
                    top = 5.dp,
                    bottom = 15.dp
                )

            ) {

                Text("Agregar al carrito")
            }
        }



        // VER CARRITO


        Button(

            onClick = abrirCarrito,

            modifier = Modifier.padding(
                top = 10.dp
            )

        ) {

            Text("🛒 Ver carrito")
        }



        // VOLVER


        Button(

            onClick = volverInicio,

            modifier = Modifier.padding(
                top = 10.dp
            )

        ) {

            Text("Volver al inicio")
        }
    }
}



// CARRITO


@Composable
fun Carrito(

    productos: List<Producto>,

    volverInicio: () -> Unit,

    eliminarProducto: (Producto) -> Unit,

    realizarPedido: () -> Unit
) {

    val total = productos.sumOf {

        it.precio
    }


    Column(

        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),

        horizontalAlignment = Alignment.CenterHorizontally
    ) {


        Text(

            text = "🛒 Carrito de compras",

            style = MaterialTheme.typography.headlineMedium
        )


        if (productos.isEmpty()) {


            Text(

                text = "El carrito está vacío",

                modifier = Modifier.padding(
                    top = 30.dp,
                    bottom = 20.dp
                )
            )


        } else {


            Text(

                text = "Productos seleccionados:",

                modifier = Modifier.padding(
                    top = 20.dp,
                    bottom = 15.dp
                )
            )



            // PRODUCTOS DEL CARRITO


            for (producto in productos) {


                Text(

                    text =
                        "${producto.emoji} ${producto.nombre}"
                )


                Text(

                    text =
                        "$${producto.precio}",

                    modifier = Modifier.padding(
                        top = 5.dp
                    )
                )


                Button(

                    onClick = {

                        eliminarProducto(producto)
                    },

                    modifier = Modifier.padding(
                        top = 5.dp,
                        bottom = 15.dp
                    )

                ) {

                    Text("Eliminar")
                }
            }



            // TOTAL


            Text(

                text = "Total: $${total}",

                style = MaterialTheme.typography.titleLarge,

                modifier = Modifier.padding(
                    top = 10.dp,
                    bottom = 20.dp
                )
            )



            // REALIZAR PEDIDO


            Button(

                onClick = realizarPedido

            ) {

                Text("Realizar pedido")
            }
        }



        // VOLVER


        Button(

            onClick = volverInicio,

            modifier = Modifier.padding(
                top = 15.dp
            )

        ) {

            Text("Volver")
        }
    }
}



// REGISTRO


@Composable
fun Registro(

    volverInicio: () -> Unit
) {

    var nombre by remember {

        mutableStateOf("")
    }


    var correo by remember {

        mutableStateOf("")
    }


    var contrasena by remember {

        mutableStateOf("")
    }


    var registroRealizado by remember {

        mutableStateOf(false)
    }


    if (registroRealizado) {


        Column(

            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),

            horizontalAlignment = Alignment.CenterHorizontally,

            verticalArrangement = Arrangement.Center
        ) {


            Text(

                text = "¡Registro exitoso!",

                style = MaterialTheme.typography.headlineMedium
            )


            Text(

                text =
                    "Bienvenido a OrganicsApp, $nombre",

                modifier = Modifier.padding(16.dp)
            )


            Button(

                onClick = volverInicio

            ) {

                Text("Volver al inicio")
            }
        }


    } else {


        Column(

            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),

            horizontalAlignment = Alignment.CenterHorizontally,

            verticalArrangement = Arrangement.Center
        ) {


            Text(

                text = "Registro de usuario",

                style = MaterialTheme.typography.headlineMedium
            )


            Text(

                text = "Crea tu cuenta en OrganicsApp",

                modifier = Modifier.padding(
                    top = 10.dp,
                    bottom = 20.dp
                )
            )


            OutlinedTextField(

                value = nombre,

                onValueChange = {

                    nombre = it
                },

                label = {

                    Text("Nombre")
                }
            )


            OutlinedTextField(

                value = correo,

                onValueChange = {

                    correo = it
                },

                label = {

                    Text("Correo electrónico")
                },

                modifier = Modifier.padding(
                    top = 10.dp
                )
            )


            OutlinedTextField(

                value = contrasena,

                onValueChange = {

                    contrasena = it
                },

                label = {

                    Text("Contraseña")
                },

                modifier = Modifier.padding(
                    top = 10.dp
                )
            )


            Button(

                onClick = {

                    if (

                        nombre.isNotBlank() &&

                        correo.isNotBlank() &&

                        contrasena.isNotBlank()
                    ) {

                        registroRealizado = true
                    }
                },

                modifier = Modifier.padding(
                    top = 20.dp
                )

            ) {

                Text("Registrarse")
            }


            Button(

                onClick = volverInicio,

                modifier = Modifier.padding(
                    top = 10.dp
                )

            ) {

                Text("Volver al inicio")
            }
        }
    }
}



// PEDIDO


@Composable
fun Pedido(

    productos: List<Producto>,

    volverInicio: () -> Unit,

    pedidoRealizado: () -> Unit
) {

    var nombreCliente by remember {

        mutableStateOf("")
    }


    var direccion by remember {

        mutableStateOf("")
    }


    var pedidoEnviado by remember {

        mutableStateOf(false)
    }


    val total = productos.sumOf {

        it.precio
    }


    if (pedidoEnviado) {


        Column(

            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),

            horizontalAlignment = Alignment.CenterHorizontally,

            verticalArrangement = Arrangement.Center
        ) {


            Text(

                text = "¡Pedido realizado!",

                style = MaterialTheme.typography.headlineMedium
            )


            Text(

                text =
                    "Gracias por tu compra, $nombreCliente",

                modifier = Modifier.padding(
                    16.dp
                )
            )


            Text(

                text =
                    "Total del pedido: $${total}"
            )


            Button(

                onClick = {

                    pedidoRealizado()

                    volverInicio()
                },

                modifier = Modifier.padding(
                    top = 20.dp
                )

            ) {

                Text("Volver al inicio")
            }
        }


    } else {


        Column(

            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),

            horizontalAlignment = Alignment.CenterHorizontally
        ) {


            Text(

                text = "Realizar pedido",

                style = MaterialTheme.typography.headlineMedium
            )


            Text(

                text =
                    "Confirma los datos de tu pedido",

                modifier = Modifier.padding(
                    top = 10.dp,
                    bottom = 20.dp
                )
            )



            // PRODUCTOS


            if (productos.isEmpty()) {


                Text(

                    text =
                        "No hay productos en el carrito",

                    modifier = Modifier.padding(
                        bottom = 20.dp
                    )
                )


            } else {


                Text(

                    text = "Productos del pedido:",

                    style =
                        MaterialTheme.typography.titleLarge,

                    modifier = Modifier.padding(
                        bottom = 10.dp
                    )
                )


                for (producto in productos) {


                    Text(

                        text =
                            "${producto.emoji} ${producto.nombre} - $${producto.precio}",

                        modifier = Modifier.padding(
                            bottom = 8.dp
                        )
                    )
                }


                Text(

                    text = "Total: $${total}",

                    style =
                        MaterialTheme.typography.titleLarge,

                    modifier = Modifier.padding(
                        top = 10.dp,
                        bottom = 20.dp
                    )
                )
            }



            // NOMBRE


            OutlinedTextField(

                value = nombreCliente,

                onValueChange = {

                    nombreCliente = it
                },

                label = {

                    Text("Nombre del cliente")
                }
            )



            // DIRECCIÓN


            OutlinedTextField(

                value = direccion,

                onValueChange = {

                    direccion = it
                },

                label = {

                    Text("Dirección de entrega")
                },

                modifier = Modifier.padding(
                    top = 10.dp
                )
            )



            // ENVIAR PEDIDO


            Button(

                onClick = {

                    if (

                        nombreCliente.isNotBlank() &&

                        direccion.isNotBlank() &&

                        productos.isNotEmpty()
                    ) {

                        pedidoEnviado = true
                    }
                },

                modifier = Modifier.padding(
                    top = 20.dp
                )

            ) {

                Text("Confirmar pedido")
            }



            // VOLVER


            Button(

                onClick = volverInicio,

                modifier = Modifier.padding(
                    top = 10.dp
                )

            ) {

                Text("Volver")
            }
        }
    }
}



// OFERTAS ESPECIALES


@Composable
fun Ofertas(

    volverInicio: () -> Unit,

    mostrarNotificacion: () -> Unit
) {

    Column(

        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),

        horizontalAlignment = Alignment.CenterHorizontally,

        verticalArrangement = Arrangement.Center
    ) {


        Text(

            text = "Ofertas especiales",

            style = MaterialTheme.typography.headlineMedium
        )


        Text(

            text =
                "Aprovecha nuestras ofertas de esta semana",

            modifier = Modifier.padding(
                top = 10.dp,
                bottom = 25.dp
            )
        )



        // MANZANA


        Text(

            text = "🍎 Manzana orgánica",

            style = MaterialTheme.typography.titleLarge
        )


        Text(

            text =
                "Antes: $2.000 - Ahora: $1.500",

            modifier = Modifier.padding(
                bottom = 20.dp
            )
        )



        // ZANAHORIA


        Text(

            text = "🥕 Zanahoria orgánica",

            style = MaterialTheme.typography.titleLarge
        )


        Text(

            text =
                "Antes: $1.500 - Ahora: $1.000",

            modifier = Modifier.padding(
                bottom = 20.dp
            )
        )



        // GRANOLA


        Text(

            text = "🌾 Granola orgánica",

            style = MaterialTheme.typography.titleLarge
        )


        Text(

            text =
                "Antes: $3.000 - Ahora: $2.500",

            modifier = Modifier.padding(
                bottom = 25.dp
            )
        )



        // NOTIFICACIÓN


        Button(

            onClick = mostrarNotificacion

        ) {

            Text("🔔 Recibir oferta")
        }



        // VOLVER


        Button(

            onClick = volverInicio,

            modifier = Modifier.padding(
                top = 10.dp
            )

        ) {

            Text("Volver al inicio")
        }
    }
}