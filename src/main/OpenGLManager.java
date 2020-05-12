package main;

import scene.Camera;
import scene.Scene;
import utils.FileUtils;
import org.lwjgl.opengl.*;

import static org.lwjgl.opengl.GL11.*;

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
    public static int programShader01 = 0;
    public static int programShader02 = 0;

    private static int loadShader(String shader) {
        int vertexShader;
        int fragmentShader;
        int programShader = ARBShaderObjects.glCreateProgramObjectARB();

        try {
            vertexShader = createShader("res/shaders/" + shader + ".vert", ARBVertexShader.GL_VERTEX_SHADER_ARB);
            fragmentShader = createShader("res/shaders/" + shader + ".frag", ARBFragmentShader.GL_FRAGMENT_SHADER_ARB);
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
        ARBShaderObjects.glAttachObjectARB(programShader, vertexShader);
        ARBShaderObjects.glAttachObjectARB(programShader, fragmentShader);

        ARBShaderObjects.glLinkProgramARB(programShader);
        if (ARBShaderObjects.glGetObjectParameteriARB(programShader, ARBShaderObjects.GL_OBJECT_LINK_STATUS_ARB) == GL11.GL_FALSE) {
            System.err.println(getLogInfo(programShader));
            return 0;
        }

        ARBShaderObjects.glValidateProgramARB(programShader);
        if (ARBShaderObjects.glGetObjectParameteriARB(programShader, ARBShaderObjects.GL_OBJECT_VALIDATE_STATUS_ARB) == GL11.GL_FALSE) {
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
            shader = ARBShaderObjects.glCreateShaderObjectARB(shaderType);

            if (shader == 0) return 0;

            ARBShaderObjects.glShaderSourceARB(shader, FileUtils.readFileAsString(filename));
            ARBShaderObjects.glCompileShaderARB(shader);

            if (ARBShaderObjects.glGetObjectParameteriARB(shader, ARBShaderObjects.GL_OBJECT_COMPILE_STATUS_ARB) == GL11.GL_FALSE) {
                throw new RuntimeException("Error creating shader: " + getLogInfo(shader));
            }

            return shader;
        } catch (Exception e) {
            ARBShaderObjects.glDeleteObjectARB(shader);
            throw e;
        }
    }

    private static String getLogInfo(int obj) {
        return ARBShaderObjects.glGetInfoLogARB(obj, ARBShaderObjects.glGetObjectParameteriARB(obj, ARBShaderObjects.GL_OBJECT_INFO_LOG_LENGTH_ARB));
    }

    public static void updateShadersUniforms() {
        /** Update programShader01 **/
        int timeUniformLocation01 = ARBShaderObjects.glGetUniformLocationARB(OpenGLManager.programShader01, "time");
        int textureUniform01 = ARBShaderObjects.glGetUniformLocationARB(OpenGLManager.programShader01, "texture01");
        int zoomUniform = ARBShaderObjects.glGetUniformLocationARB(OpenGLManager.programShader01, "zoom");
        int widthHeightRatio = ARBShaderObjects.glGetUniformLocationARB(OpenGLManager.programShader01, "widthHeightRatio");
        int lightSourceUniform01 = ARBShaderObjects.glGetUniformLocationARB(OpenGLManager.programShader01, "lightSource01");
        int lightSourceUniform02 = ARBShaderObjects.glGetUniformLocationARB(OpenGLManager.programShader01, "lightSource02");
        int lightSourceUniform03 = ARBShaderObjects.glGetUniformLocationARB(OpenGLManager.programShader01, "lightSource03");
        ARBShaderObjects.glUseProgramObjectARB(OpenGLManager.programShader01);
        ARBShaderObjects.glUniform1fARB(timeUniformLocation01, (float) GameStatus.getRuntime());
        ARBShaderObjects.glUniform1iARB(textureUniform01, 0);
        ARBShaderObjects.glUniform1fARB(zoomUniform, (float) Camera.getZoom());
        ARBShaderObjects.glUniform1fARB(widthHeightRatio, ((float) Window.getWidth() / (float) Window.getHeight()));

        if (Scene.getListOfLightSources().size() >= 1) {
            Coordinates lightSourceOpenGLCoordinates01 = Coordinates.cameraToOpenGLCoordinates(Scene.getListOfLightSources().get(Scene.getListOfLightSources().size() - 1).getCameraCoordinates());
            float[] lightSource01 = new float[]{(float) lightSourceOpenGLCoordinates01.x, - (float) lightSourceOpenGLCoordinates01.y};
            ARBShaderObjects.glUniform2fvARB(lightSourceUniform01, lightSource01);
        }

        if (Scene.getListOfLightSources().size() >= 2) {
            Coordinates lightSourceOpenGLCoordinates02 = Coordinates.cameraToOpenGLCoordinates(Scene.getListOfLightSources().get(Scene.getListOfLightSources().size() - 2).getCameraCoordinates());
            float[] lightSource02 = new float[]{(float) lightSourceOpenGLCoordinates02.x, - (float) lightSourceOpenGLCoordinates02.y};
            ARBShaderObjects.glUniform2fvARB(lightSourceUniform02, lightSource02);
        }

        if (Scene.getListOfLightSources().size() >= 3) {
            Coordinates lightSourceOpenGLCoordinates03 = Coordinates.cameraToOpenGLCoordinates(Scene.getListOfLightSources().get(Scene.getListOfLightSources().size() - 3).getCameraCoordinates());
            float[] lightSource03 = new float[]{(float) lightSourceOpenGLCoordinates03.x, - (float) lightSourceOpenGLCoordinates03.y};
            ARBShaderObjects.glUniform2fvARB(lightSourceUniform03, lightSource03);
        }

        /** Update programShader02 **/
        int timeUniformLocation02 = ARBShaderObjects.glGetUniformLocationARB(OpenGLManager.programShader02, "time");
        ARBShaderObjects.glUseProgramObjectARB(OpenGLManager.programShader02);
        ARBShaderObjects.glUniform1fARB(timeUniformLocation02, (float) GameStatus.getRuntime());
    }

    public static void useShader(int shader) {
//        System.out.println("Use shader " + shader);
        switch (shader) {
            case 0:
            default:
                ARBShaderObjects.glUseProgramObjectARB(0);
                break;
            case 1:
                ARBShaderObjects.glUseProgramObjectARB(OpenGLManager.programShader01);
                break;
            case 2:
                ARBShaderObjects.glUseProgramObjectARB(OpenGLManager.programShader02);
                break;
        }
    }

    public static void releaseCurrentShader() {
        useShader(0);
    }
}
