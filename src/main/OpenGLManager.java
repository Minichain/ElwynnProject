package main;

import entities.LightSource;
import org.lwjgl.BufferUtils;
import scene.Camera;
import scene.Scene;
import utils.FileUtils;
import org.lwjgl.opengl.*;

import java.nio.FloatBuffer;
import java.util.Arrays;

import static org.lwjgl.opengl.GL20.*;

public class OpenGLManager {
    public static int GPU_CALLS;

    public static void glBegin(int mode) {
        GL11.glBegin(mode);
        GPU_CALLS++;
    }

    /**
     * program shader, to which is attached a vertex and fragment shaders.
     * They are set to 0 as a check because GL will assign unique int
     * values to each
     */
    private static final boolean ARB_SHADERS = true;
    private static final int VERTEX_SHADER = ARB_SHADERS ? ARBVertexShader.GL_VERTEX_SHADER_ARB : GL_VERTEX_SHADER;
    private static final int FRAGMENT_SHADER = ARB_SHADERS ? ARBFragmentShader.GL_FRAGMENT_SHADER_ARB : GL_FRAGMENT_SHADER;

    public static int programShader01 = 0;
    public static int programShader02 = 0;

    private static int loadShader(String shader) {
        int vertexShader;
        int fragmentShader;
        int programShader = GL20.glCreateProgram();

        try {
            vertexShader = createShader("res/shaders/" + shader + ".vert", VERTEX_SHADER);
            fragmentShader = createShader("res/shaders/" + shader + ".frag", FRAGMENT_SHADER);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }

        if (vertexShader == 0 || fragmentShader == 0 || programShader == 0) return 0;

        /**
         * If the vertex and fragment shaders setup successfully,
         * attach them to the shader program, link the shader program,
         * and validate.
         */
        glAttachShader(programShader, vertexShader);
        glAttachShader(programShader, fragmentShader);

        glLinkProgram(programShader);
        if (glGetProgrami(programShader, GL20.GL_LINK_STATUS) == GL11.GL_FALSE) {
            System.err.println(getLogInfo(programShader));
            return 0;
        }

        glValidateProgram(programShader);
        if (glGetProgrami(programShader, GL20.GL_VALIDATE_STATUS) == GL11.GL_FALSE) {
            System.err.println(getLogInfo(programShader));
            return 0;
        }

        return programShader;
    }

    public static void prepareOpenGL() {
        GL.createCapabilities();

        //Load shaders
        programShader01 = loadShader("shader01");
        programShader02 = loadShader("shader02");
    }

    public static void prepareFrame() {
//        System.out.println("Preparing frame!");

        GPU_CALLS = 0;

        //Clear frame
        glClear(GL_COLOR_BUFFER_BIT);

        glLoadIdentity(); //Cleans out any matrix mode
        glOrtho(0f, Parameters.getResolutionWidth(), Parameters.getResolutionHeight(), 0f, 1f, -1f);
    }

    public static void drawTexture(int x, int y, float u, float v, float u2, float v2, float spriteWidth, float spriteHeight) {
        drawTexture(x, y, u, v, u2, v2, spriteWidth, spriteHeight, 1f, 1f, 1f, 1f);
    }

    public static void drawTexture(int x, int y, float u, float v, float u2, float v2, float spriteWidth, float spriteHeight, float r, float g, float b) {
        drawTexture(x, y, u, v, u2, v2, spriteWidth, spriteHeight, 1f, r, g, b);
    }

    public static void drawTexture(int x, int y, float u, float v, float u2, float v2, float spriteWidth, float spriteHeight, float transparency, float r, float g, float b) {
        glColor4f(r, g, b, transparency);
        glTexCoord2f(u, v);
        glVertex2f(x, y);
        glTexCoord2f(u, v2);
        glVertex2f(x, y + spriteHeight);
        glTexCoord2f(u2, v2);
        glVertex2f(x + spriteWidth, y + spriteHeight);
        glTexCoord2f(u2, v);
        glVertex2f(x + spriteWidth, y);
    }

    public static void drawRectangle(int x, int y, float width, float height) {
        drawRectangle(x, y, width, height, 1.0, 1f, 1f, 1f);
    }

    public static void drawRectangle(int x, int y, float width, float height, float greyValue) {
        drawRectangle(x, y, width, height, 1.0, greyValue, greyValue, greyValue);
    }

    public static void drawRectangle(int x, int y, float width, float height, double transparency, float greyValue) {
        drawRectangle(x, y, width, height, transparency, greyValue, greyValue, greyValue);
    }

    public static void drawRectangle(int x, int y, float width, float height, double transparency, float r, float g, float b) {
        glColor4f(r, g, b, (float) transparency);
        glVertex2f(x, y);
        glVertex2f(x, y + height);
        glVertex2f(x + width, y);
        glVertex2f(x + width, y);
        glVertex2f(x, y + height);
        glVertex2f(x + width, y + height);
    }

    public static void drawTriangle(Coordinates vertex1, Coordinates vertex2, Coordinates vertex3, double transparency, float greyValue) {
        drawTriangle(
                new int[]{(int) vertex1.x, (int) vertex1.y},
                new int[]{(int) vertex2.x, (int) vertex2.y},
                new int[]{(int) vertex3.x, (int) vertex3.y},
                transparency, greyValue, greyValue, greyValue);
    }

    public static void drawTriangle(int[] vertex1, int[] vertex2, int[] vertex3, double transparency, float greyValue) {
        drawTriangle(vertex1, vertex2, vertex3, transparency, greyValue, greyValue, greyValue);
    }

    public static void drawTriangle(int[] vertex1, int[] vertex2, int[] vertex3, double transparency, float r, float g, float b) {
        glColor4f(r, g, b, (float) transparency);
        glVertex2f(vertex1[0], vertex1[1]);
        glVertex2f(vertex2[0], vertex2[1]);
        glVertex2f(vertex3[0], vertex3[1]);
    }

    /*
     * With the exception of syntax, setting up vertex and fragment shaders
     * is the same.
     * @param the name and path to the vertex shader
     */
    private static int createShader(String filename, int shaderType) throws Exception {
        int shader = 0;
        try {
            shader = glCreateShader(shaderType);

            if (shader == 0) return 0;

            glShaderSource(shader, FileUtils.readFileAsString(filename));
            glCompileShader(shader);

            if (glGetProgrami(shader, GL20.GL_COMPILE_STATUS) == GL20.GL_FALSE) {
                throw new RuntimeException("Error creating shader: " + getLogInfo(shader));
            }

            return shader;
        } catch (Exception e) {
            glDeleteShader(shader);
            throw e;
        }
    }

    public static void updateShadersUniforms() {
        /** Update programShader01 **/
        int timeUniformLocation01 = glGetUniformLocation(OpenGLManager.programShader01, "time");
        int textureUniform01 = glGetUniformLocation(OpenGLManager.programShader01, "texture01");
        int zoomUniform = glGetUniformLocation(OpenGLManager.programShader01, "zoom");
        int widthHeightRatio = glGetUniformLocation(OpenGLManager.programShader01, "widthHeightRatio");
        int lightSourcesUniform = glGetUniformLocation(OpenGLManager.programShader01, "lightSources");
        glUseProgram(OpenGLManager.programShader01);
        glUniform1f(timeUniformLocation01, (float) GameStatus.getRuntime());
        glUniform1i(textureUniform01, 0);
        glUniform1f(zoomUniform, (float) Camera.getZoom());
        glUniform1f(widthHeightRatio, ((float) Window.getWidth() / (float) Window.getHeight()));

        int i = 0;
        int size = 64 * 2;
        float[] lightSources = new float[size];
        Arrays.fill(lightSources, -10000f);
        for (LightSource lightSource : Scene.getListOfLightSources()) {
            Coordinates lightSourceOpenGLCoordinates = Coordinates.cameraToOpenGLCoordinates(lightSource.getCameraCoordinates());
            lightSources[i] = (float) lightSourceOpenGLCoordinates.x;
            lightSources[i + 1] = - (float) lightSourceOpenGLCoordinates.y;
            i += 2;
        }

        FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(size);
        floatBuffer.put(lightSources);
        floatBuffer.flip();
        glUniform2fv(lightSourcesUniform, floatBuffer);

        /** Update programShader02 **/
        int timeUniformLocation02 = GL20.glGetUniformLocation(OpenGLManager.programShader02, "time");
        glUseProgram(OpenGLManager.programShader02);
        glUniform1f(timeUniformLocation02, (float) GameStatus.getRuntime());
    }

    public static void useShader(int shader) {
//        System.out.println("Use shader " + shader);
        switch (shader) {
            case 0:
            default:
                glUseProgram(0);
                break;
            case 1:
                glUseProgram(OpenGLManager.programShader01);
                break;
            case 2:
                glUseProgram(OpenGLManager.programShader02);
                break;
        }
    }

    public static void releaseCurrentShader() {
        useShader(0);
    }

    /**
     * Methods depending on if "ARB_SHADERS" is ENABLED / DISABLED
     **/

    private static void glDeleteShader(int shader) {
        if (ARB_SHADERS) {
            ARBShaderObjects.glDeleteObjectARB(shader);
        } else {
            GL20.glDeleteShader(shader);
        }
    }

    private static void glCompileShader(int shader) {
        if (ARB_SHADERS) {
            ARBShaderObjects.glCompileShaderARB(shader);
        } else {
            GL20.glCompileShader(shader);
        }
    }

    private static void glShaderSource(int shader, String file) {
        if (ARB_SHADERS) {
            ARBShaderObjects.glShaderSourceARB(shader, file);
        } else {
            GL20.glShaderSource(shader, file);
        }
    }

    private static int glCreateShader(int shaderType) {
        if (ARB_SHADERS) {
            return ARBShaderObjects.glCreateShaderObjectARB(shaderType);
        } else {
            return GL20.glCreateShader(shaderType);
        }
    }

    private static String getLogInfo(int obj) {
        if (ARB_SHADERS) {
            return ARBShaderObjects.glGetInfoLogARB(obj, glGetProgrami(obj, ARBShaderObjects.GL_OBJECT_INFO_LOG_LENGTH_ARB));
        } else {
            return GL20.glGetProgramInfoLog(obj, glGetProgrami(obj, GL20.GL_INFO_LOG_LENGTH));
        }
    }

    private static int glGetUniformLocation(int shader, String uniformName) {
        if (ARB_SHADERS) {
            return ARBShaderObjects.glGetUniformLocationARB(shader, uniformName);
        } else {
            return GL20.glGetUniformLocation(shader, uniformName);
        }
    }

    private static void glUseProgram(int shader) {
        if (ARB_SHADERS) {
            ARBShaderObjects.glUseProgramObjectARB(shader);
        } else {
            GL20.glUseProgram(shader);
        }
    }

    private static void glUniform1f(int uniformLocation, float uniformValue) {
        if (ARB_SHADERS) {
            ARBShaderObjects.glUniform1fARB(uniformLocation, uniformValue);
        } else {
            GL20.glUniform1f(uniformLocation, uniformValue);
        }
    }

    private static void glUniform1i(int uniformLocation, int uniformValue) {
        if (ARB_SHADERS) {
            ARBShaderObjects.glUniform1iARB(uniformLocation, uniformValue);
        } else {
            GL20.glUniform1i(uniformLocation, uniformValue);
        }
    }

    private static void glUniform2fv(int uniformLocation, FloatBuffer uniformValue) {
        if (ARB_SHADERS) {
            ARBShaderObjects.glUniform2fvARB(uniformLocation, uniformValue);
        } else {
            GL20.glUniform2fv(uniformLocation, uniformValue);
        }
    }

    private static void glValidateProgram(int programShader) {
        if (ARB_SHADERS) {
            ARBShaderObjects.glValidateProgramARB(programShader);
        } else {
            GL20.glValidateProgram(programShader);
        }
    }

    private static int glGetProgrami(int programShader, int status) {
        if (ARB_SHADERS) {
            return ARBShaderObjects.glGetObjectParameteriARB(programShader, status);
        } else {
//            return GL20.glGetShaderi(programShader, status);
            return GL20.glGetProgrami(programShader, status);
        }
    }

    private static void glAttachShader(int programShader, int vertexShader) {
        if (ARB_SHADERS) {
            ARBShaderObjects.glAttachObjectARB(programShader, vertexShader);
        } else {
            GL20.glAttachShader(programShader, vertexShader);
        }
    }

    private static void glLinkProgram(int programShader) {
        if (ARB_SHADERS) {
            ARBShaderObjects.glLinkProgramARB(programShader);
        } else {
            GL20.glLinkProgram(programShader);
        }
    }
}
