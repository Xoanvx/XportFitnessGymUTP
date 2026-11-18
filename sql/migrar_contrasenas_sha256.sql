/*
  Migra las contraseñas guardadas en texto plano en dbo.Usuario al mismo formato
  que genera com.registro.dao.Seguridad.encriptarContraseña:

      Base64( SHA-256( UTF-16LE( UPPER( TRIM(clave) ) ) ) )   -> 44 caracteres

  Las contraseñas que ya tienen 44 caracteres terminados en '=' se consideran
  encriptadas y NO se tocan (así el script se puede ejecutar más de una vez).
*/

-- 1) DIAGNÓSTICO: ¿qué usuarios tienen la clave en texto plano?
SELECT Usuario,
       LEN([Contraseña]) AS Largo,
       CASE WHEN LEN([Contraseña]) = 44 AND RIGHT([Contraseña], 1) = '='
            THEN 'ENCRIPTADA' ELSE 'TEXTO PLANO' END AS Formato
FROM dbo.Usuario;
GO

-- 2) PRUEBA: debe devolver exactamente  lNs01GlWt7hnoXoaNyxc0oAmShBlQeeJ1ZHHa+G9QTQ=
--    (es el valor que calcula Seguridad.java para la clave 'prueba123')
DECLARE @h VARBINARY(32) = HASHBYTES('SHA2_256', UPPER(LTRIM(RTRIM(N'prueba123'))));
SELECT CAST('' AS XML).value('xs:base64Binary(sql:variable("@h"))', 'VARCHAR(64)') AS HashPrueba;
GO

-- 3) MIGRACIÓN (ejecutar solo si la prueba del paso 2 coincide)
BEGIN TRANSACTION;

UPDATE u
SET [Contraseña] = CAST('' AS XML).value('xs:base64Binary(sql:column("h.Hash"))', 'VARCHAR(64)')
FROM dbo.Usuario u
CROSS APPLY (SELECT HASHBYTES('SHA2_256',
                 UPPER(LTRIM(RTRIM(CONVERT(NVARCHAR(255), u.[Contraseña]))))) AS Hash) h
WHERE ISNULL(u.[Contraseña], '') <> ''
  AND NOT (LEN(u.[Contraseña]) = 44 AND RIGHT(u.[Contraseña], 1) = '=');

SELECT @@ROWCOUNT AS UsuariosMigrados;

-- Revise el resultado y luego confirme con COMMIT (o deshaga con ROLLBACK)
-- COMMIT TRANSACTION;
-- ROLLBACK TRANSACTION;
