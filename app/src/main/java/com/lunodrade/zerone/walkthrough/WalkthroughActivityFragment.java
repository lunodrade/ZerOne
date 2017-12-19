package com.lunodrade.zerone.walkthrough;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import com.lunodrade.zerone.R;

import java.util.ArrayList;
import java.util.List;

import me.relex.circleindicator.CircleIndicator;

/**
 * The only thing you need to do is customize the {@link #initSections()} method
 * and add your walkthrough sections there
 * <p>
 * Created by fahad on 21/09/2016.
 */

public class WalkthroughActivityFragment extends Fragment implements ViewPager.OnPageChangeListener {
    //region Properties
    ViewGroup viewRoot;
    ViewGroup topLabelsContainer;
    ViewGroup topNameContainer;

    TextView lbl2DoName;
    TextView lbl2DoVersion;
    TextView lblWelcomeTitle;
    TextView lblWatchVideo;

    ViewGroup phoneContainer;
    AppCompatImageView imgPhoneFrame;
    ScaleImageView imgPhoneShot;
    ViewGroup bottomContainer;
    ViewPager tutorialPager;
    CircleIndicator circularIndicator;
    AppCompatButton btnDone;


    List<WalkthroughSection> walkthroughSectionList = new ArrayList<>();
    private int bounceDPs = 10;

    private int screenHeight;
    private int screenWidth;

    private int phoneContainerHeight;
    private int phoneContainerWidth;

    private int phoneLeft;
    private int phoneContainerTop;

    private int bottomContainerTop;
    private int tutorialButtonBottom;
    private Handler uiHandler;
    private WalkthroughInternalPagerAdapter pagerAdapter;

    //endregion

    //region View Creation
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_walkthrough, container, false);

        viewRoot = (ViewGroup) rootView.findViewById(R.id.viewRoot);
        topLabelsContainer = (ViewGroup) rootView.findViewById(R.id.topLabelsContainer);
        topNameContainer = (ViewGroup) rootView.findViewById(R.id.topNameContainer);

        lbl2DoName = (TextView) rootView.findViewById(R.id.lbl2DoName);
        lbl2DoVersion = (TextView) rootView.findViewById(R.id.lbl2DoVersion);
        lblWelcomeTitle = (TextView) rootView.findViewById(R.id.lblWelcomeTitle);

        lblWatchVideo = (TextView) rootView.findViewById(R.id.lblWatchVideo);

        phoneContainer = (ViewGroup) rootView.findViewById(R.id.phoneContainer);
        imgPhoneFrame = (AppCompatImageView) rootView.findViewById(R.id.imgPhoneFrame);
        imgPhoneShot = (ScaleImageView) rootView.findViewById(R.id.imgPhoneShot);

        bottomContainer = (ViewGroup) rootView.findViewById(R.id.bottomContainer);
        tutorialPager = (ViewPager) rootView.findViewById(R.id.tutorialPager);

        circularIndicator = (CircleIndicator) rootView.findViewById(R.id.circularIndicator);

        btnDone = (AppCompatButton) rootView.findViewById(R.id.btnDone);
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doneClicked();
            }
        });

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initUI();
    }

    private void initUI() {
        uiHandler = new Handler(Looper.getMainLooper());

        initSections();

        pagerAdapter = new WalkthroughInternalPagerAdapter(getActivity().getSupportFragmentManager());
        tutorialPager.setAdapter(pagerAdapter);
        tutorialPager.addOnPageChangeListener(this);
        circularIndicator.setViewPager(tutorialPager);

        lblWatchVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("")));
            }
        });

        viewRoot.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft,
                                       int oldTop, int oldRight, int oldBottom) {
                // its possible that the layout is not complete in which case
                // we will get all zero values for the positions, so ignore the event
                if (left == 0 && top == 0 && right == 0 && bottom == 0) {
                    return;
                }

                viewRoot.removeOnLayoutChangeListener(this);

                phoneContainerHeight = phoneContainer.getHeight();
                phoneContainerWidth = phoneContainer.getWidth();
                phoneLeft = phoneContainer.getLeft();

                screenHeight = viewRoot.getHeight();
                screenWidth = viewRoot.getWidth();

                bottomContainerTop = (int) bottomContainer.getY();
                tutorialButtonBottom = (int) lblWatchVideo.getBottom();

                phoneContainerTop = (int) phoneContainer.getY();

                loadImageResource(R.drawable.tutorial_home_b);

                showPhone(-5, 0, 0, false, null);

                uiHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        revealPhoneFirstTime(700, false);
                    }
                }, 200);
            }
        });

        phoneContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tutorialPager.getCurrentItem() == 0) {
                    revealPhoneFirstTime(300, true);
                }
            }
        });
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                  This is the only section you need to customize in order to get as many Walkthrough Sections as you want
    //
    //////////////////////////////////////////////////////////////////////////////////////////////*/
    private void initSections() {
        lbl2DoVersion.setText("| v1.1");
        WalkthroughSection section = new WalkthroughSection();

        section.sectionTitle = "Zerone";
        section.sectionDescription = "É um aplicativo pra ajudar na aprendizagem de Algoritmos e Programação II. " +
                "Este tutorial irá lhe mostrar alguns detalhes de telas e seus funcionamentos.\n\n" +
                "Deslize para a direita e leia o tutorial.";
        section.sectionDeviceVerticalPercToShow = 25;
        section.sectionDeviceHorizontalPercToShow = 0;
        section.sectionDeviceZoomPercToShow = 0;
        section.sectionShotName = R.drawable.tutorial_home_b;
        walkthroughSectionList.add(section);

        section = new WalkthroughSection();
        section.sectionTitle = "Rever tutorial";
        section.sectionDescription = "Antes de tudo, saiba que você pode rever esse tutorial a qualquer momento clicando neste botão, localizado no topo da tela \"Início\".";
        section.sectionDeviceVerticalPercToShow = 30;
        section.sectionDeviceHorizontalPercToShow = 0;
        section.sectionDeviceZoomPercToShow = 0;
        section.sectionShotName = R.drawable.tutorial_home_a;
        walkthroughSectionList.add(section);

        section = new WalkthroughSection();
        section.sectionTitle = "Menu no rodapé";
        section.sectionDescription = "Aqui você tem acesso as 3 principais telas do app.\n\n" +
                "\"Início\": é onde você tem acesso aos livros de exercícios.\n\n" +
                "\"Salas\": é onde você pode ingressar em uma turma, ou caso já esteja em uma, você poderá ver o ranking da mesma.\n\n" +
                "\"Perfil\": aqui você vê informações pessoais sobre sua conta, como seu XP e Nível, além de medalhas que conquistou.";
        section.sectionDeviceVerticalPercToShow = 100;
        section.sectionDeviceHorizontalPercToShow = 0;
        section.sectionDeviceZoomPercToShow = 5;
        section.sectionShotName = R.drawable.tutorial_home_a;
        walkthroughSectionList.add(section);

        section = new WalkthroughSection();
        section.sectionTitle = "Livros";
        section.sectionDescription = "Ao clicar no livro, você acessa a parte de realizar exercícios. \n\n" +
                "Exercícios são um conjunto de 15 questões, com questões aleatórias a cada vez que você inicia um novo livro. Você pode ter 3 erros no máximo, " +
                "e caso consiga realizar o exercício sem ultrapassar essa quantidade, você ganha pontos de XP. \n\n" +
                "Ao ganhar pontos de XP você sobe de nível, e então desbloqueia novos livros, com novos conteúdos.";
        section.sectionDeviceVerticalPercToShow = 75;
        section.sectionDeviceHorizontalPercToShow = -10;
        section.sectionDeviceZoomPercToShow = -10;
        section.sectionShotName = R.drawable.tutorial_home_a;
        walkthroughSectionList.add(section);

        section = new WalkthroughSection();
        section.sectionTitle = "Desbloquear livros";
        section.sectionDescription = "Se o botão do livro estiver cinza, significa que ele ainda bloqueado para você. \n\n" +
                "Então preste atenção no valor de nível que diz no lugar do botão. Aquele valor é o nível mínimo que o livro pede como exigência.";
        section.sectionDeviceVerticalPercToShow = 75;
        section.sectionDeviceHorizontalPercToShow = 10;
        section.sectionDeviceZoomPercToShow = -10;
        section.sectionShotName = R.drawable.tutorial_home_b;
        walkthroughSectionList.add(section);

        section = new WalkthroughSection();
        section.sectionTitle = "Salas";
        section.sectionDescription = "Você pode aprender sozinho, mas pode ser muito mais útil fazendo isso em grupo. \n\n" +
                "Ingresse em uma turma e então ao ganhar pontos de exercícios, eles também irão para o ranking dessa turma. \n\n" +
                "Você só pode estar em uma turma por vez.";
        section.sectionDeviceVerticalPercToShow = 85;
        section.sectionDeviceHorizontalPercToShow = 0;
        section.sectionDeviceZoomPercToShow = 10;
        section.sectionShotName = R.drawable.tutorial_salas_a;
        walkthroughSectionList.add(section);

        section = new WalkthroughSection();
        section.sectionTitle = "Salas - ranking";
        section.sectionDescription = "Ao fazer parte de uma turma, você tem acesso a visualizar o ranking da mesma. \n\n" +
                "Uma turma pode ser um grupo criado entre amigos para competição interna, ou então criado por um professor afim de observar o desempenho de seus alunos.";
        section.sectionDeviceVerticalPercToShow = 90;
        section.sectionDeviceHorizontalPercToShow = 0;
        section.sectionDeviceZoomPercToShow = 15;
        section.prolongZoomIn = 2;
        section.sectionShotName = R.drawable.tutorial_salas_b;
        walkthroughSectionList.add(section);

        section = new WalkthroughSection();
        section.sectionTitle = "Salas - sair";
        section.sectionDescription = "\n" +
                "Você pode sair de uma sala ao clicar no botão superior direito, porém se optar por realmente sair, " +
                "você perderá todos pontos acumulados durante sua presente nela (e mesmo que volte para ela, voltará do 0).";
        section.sectionDeviceVerticalPercToShow = 20;
        section.sectionDeviceHorizontalPercToShow = 0;
        section.sectionDeviceZoomPercToShow = 20;
        section.sectionShotName = R.drawable.tutorial_salas_b;
        walkthroughSectionList.add(section);

        section = new WalkthroughSection();
        section.sectionTitle = "Perfil - nível e xp";
        section.sectionDescription = "Aqui você pode ver o seu XP (que são pontos ganhos através da realização de exercícios e obtendo " +
                "achievements - que são medalhas ao realizar algum feito dentro do aplicativo). \n\n" +
                "Seu nível é automático e é de acordo com a sua quantidade de XP (o nível basicamente é usado como parâmetro para desbloquear novos livros).";
        section.sectionDeviceVerticalPercToShow = 57;
        section.sectionDeviceHorizontalPercToShow = 0;
        section.sectionDeviceZoomPercToShow = -15;
        section.sectionShotName = R.drawable.tutorial_perfil;
        walkthroughSectionList.add(section);

        section = new WalkthroughSection();
        section.sectionTitle = "Perfil - achievements";
        section.sectionDescription = "Aqui você pode ver a lista de achievements que já ganhou, e o quanto de XP recebeu por cada uma delas. " +
                "Imagine isso como um quadro para guardar e exibir suas medalhas :)\n\n" +
                "Achievements são ganhos automaticamente durante o uso do aplicativo. Eles te dão uma medalha e um valor de XP. \n\n" +
                "Você pode receber o seu primeiro ao entrar em uma turma ;) ";
        section.sectionDeviceVerticalPercToShow = 100;
        section.sectionDeviceHorizontalPercToShow = 0;
        section.sectionDeviceZoomPercToShow = 35;
        section.sectionShotName = R.drawable.tutorial_perfil;
        walkthroughSectionList.add(section);

        section = new WalkthroughSection();
        section.sectionTitle = "Exercício - abas";
        section.sectionDescription = "Na aba \"exercícios\" você irá realizar as questões. " +
                "Enquanto na aba \"estudos\" você tem acesso a um conteúdo separado por tópicos e que é parte do que será visto nas questões. \n\n" +
                "Você pode trocar de aba durante os exercícios, e assim tirar qualquer dúvida antes mesmo de responder a questão.";
        section.sectionDeviceVerticalPercToShow = 40;
        section.sectionDeviceHorizontalPercToShow = 0;
        section.sectionDeviceZoomPercToShow = 35;
        section.sectionShotName = R.drawable.tutorial_question_abas;
        walkthroughSectionList.add(section);

        section = new WalkthroughSection();
        section.sectionTitle = "Exercício - estudos";
        section.sectionDescription = "Na aba \"estudos\" você tem acesso a todo conteúdo relacionada as questões do livro que escolheu.";
        section.sectionDeviceVerticalPercToShow = 65;
        section.sectionDeviceHorizontalPercToShow = 0;
        section.sectionDeviceZoomPercToShow = 10;
        section.sectionShotName = R.drawable.tutorial_question_estudo;
        walkthroughSectionList.add(section);

        section = new WalkthroughSection();
        section.sectionTitle = "Exercício - barra superior";
        section.sectionDescription = "No ponto A você tem a seta de voltar, para caso queira cancelar o exercício. " +
                "Ao sair você perde todo o andamento, e iniciará do zero na próxima vez.\n\n" +
                "No ponto B você tem o novo do livro ao qual acessou, para garantir que não entrou em algum por erro.";
        section.sectionDeviceVerticalPercToShow = 1;
        section.sectionDeviceHorizontalPercToShow = 70;
        section.sectionDeviceZoomPercToShow = 100;
        section.sectionShotName = R.drawable.tutorial_question_radio_ab;
        walkthroughSectionList.add(section);

        section = new WalkthroughSection();
        section.sectionTitle = "Exercício - barra superior";
        section.sectionDescription = "No ponto C você tem um cronômetro. Ele marca o tempo desde que você iniciou a primeira questão, " +
                "e será parado caso perceba que você não esta interagindo com o aplicativo ou que tenha mudado para outro aplicativo durante o exercício.\n\n" +
                "O intuito aqui é utilizar a técnica Pomodoro. Uma técnica de concentração que diz: uma pessoa ao se concentrar em " +
                "apenas uma tarefa duramente X minutos, sem interrupções externas (como outros aplicativos), " +
                "terá uma melhor absorvição daquilo em que se concentrou. \n\n" +
                "Você receberá mais pontos ao concluir o exercício se terminar em um tempo próximo aos de 15 minutos. " +
                "Então aproveite para ler o conteúdo de estudo, se realizar as questões rapidamente.";
        section.sectionDeviceVerticalPercToShow = 1;
        section.sectionDeviceHorizontalPercToShow = -70;
        section.sectionDeviceZoomPercToShow = 100;
        section.sectionShotName = R.drawable.tutorial_question_radio_c;
        walkthroughSectionList.add(section);

        section = new WalkthroughSection();
        section.sectionTitle = "Exercício - barra superior";
        section.sectionDescription = "No ponto D você terá acesso a qual questão está, e quantas questões são o exercício. \n" +
                "Exemplo: 15/15 significa que é a última questão do exercício.\n\n" +
                "No ponto E você tem acesso a quantidade de \"vidas\" que ainda possui naquele exercício. \n" +
                "Você inicia com 3 vidas, e a cada questão errada perde uma vida. Se você possuir uma vida e errar a questão, " +
                "o exercício terminará de forma não concluída, e você voltará para a tela \"Início\".";
        section.sectionDeviceVerticalPercToShow = 1;
        section.sectionDeviceHorizontalPercToShow = -70;
        section.sectionDeviceZoomPercToShow = 100;
        section.sectionShotName = R.drawable.tutorial_question_radio_de;
        walkthroughSectionList.add(section);

        section = new WalkthroughSection();
        section.sectionTitle = "Exercício - errar";
        section.sectionDescription = "Ao errar uma questão, você irá perder uma vida (como já explicado) e também irá visualizar essa janela de fundo vermelho. \n\n" +
                "Nessa janela você irá ver uma resposta de apoio, que pode lhe auxiliar para saber onde errou.\n\n" +
                "Você pode clicar \"próxima\" para ir a próxima questão, ou então ir na aba \"estudos\" e se informar sobre o erro.";
        section.sectionDeviceVerticalPercToShow = 100;
        section.sectionDeviceHorizontalPercToShow = 0;
        section.sectionDeviceZoomPercToShow = 10;
        section.sectionShotName = R.drawable.tutorial_question_errada;
        walkthroughSectionList.add(section);

        section = new WalkthroughSection();
        section.sectionTitle = "Exercício - tipo de questões";
        section.sectionDescription = "Nesse tipo de questão, com a opção das alternativas sendo \"redonda\", " +
                "você só pode marcar uma opção. \n\nApenas uma opção desse tipo de questão é a correta.";
        section.sectionDeviceVerticalPercToShow = 75;
        section.sectionDeviceHorizontalPercToShow = 0;
        section.sectionDeviceZoomPercToShow = 0;
        section.sectionShotName = R.drawable.tutorial_question_radio_ab;
        walkthroughSectionList.add(section);

        section = new WalkthroughSection();
        section.sectionTitle = "Exercício - tipo de questões";
        section.sectionDescription = "Nesse tipo de questão, com a opção das alternativas sendo \"quadrada\". " +
                "Você pode marcar quantas opções quiser. Sendo que uma ou mais delas estão corretas. \n\n" +
                "Se a questão tiver duas ou mais alternativas corretas, e você marcar apenas uma delas, " +
                "a questão será dada como errada, você precisa marcar exatamente todas opções corretas.";
        section.sectionDeviceVerticalPercToShow = 76;
        section.sectionDeviceHorizontalPercToShow = 0;
        section.sectionDeviceZoomPercToShow = -5;
        section.sectionShotName = R.drawable.tutorial_question_check;
        walkthroughSectionList.add(section);

        section = new WalkthroughSection();
        section.sectionTitle = "Exercício - tipo de questões";
        section.sectionDescription = "Nesse tipo de questão será apresentada uma pergunta em um estilo mais aberto, " +
                "e sua resposta será digitada de modo livre, podendo ser quaisquer palavras e valores.\n\n" +
                "Fim. Obrigado por ler, clique em \"Entendi\" e inicie o uso do aplicativo.";
        section.sectionDeviceVerticalPercToShow = 73;
        section.sectionDeviceHorizontalPercToShow = 0;
        section.sectionDeviceZoomPercToShow = 10;
        section.sectionShotName = R.drawable.tutorial_question_input;
        walkthroughSectionList.add(section);

    }
    //endregion

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    //                  T
    //
    //////////////////////////////////////////////////////////////////////////////////////////////*/

    //region Animations
    private void revealPhoneFirstTime(int duration, final boolean clicked) {
        // Show 25% above rim, but 56 below the tutorial button
        int twentyFiveAboverim = (bottomContainerTop - phoneContainerTop) - ((phoneContainerHeight * 25) / 100);
        int distanceFromCenterofPhonetoRight = phoneContainerWidth / 2;

        if (twentyFiveAboverim - convertDpToPx(getActivity(), 48) <= tutorialButtonBottom) {
            twentyFiveAboverim = (int) (tutorialButtonBottom + convertDpToPx(getActivity(), 48));
        }

        if (clicked) {
            twentyFiveAboverim -= convertDpToPx(getActivity(), 25);
        }

        if (lblWelcomeTitle != null) {
            lblWelcomeTitle.animate().alpha(1);
            lblWatchVideo.animate().alpha(1);
            lbl2DoName.animate().alpha(1);
            lbl2DoVersion.animate().alpha(1);

            phoneContainer.animate().withLayer().translationY(twentyFiveAboverim).translationX(0)
                    .scaleX(1).scaleY(1).setDuration(duration).setInterpolator(new DecelerateInterpolator())
                    .setListener(new AnimatorListenerAdapter() {
                        boolean gotCancelled = false;

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);

                            if (!gotCancelled) {
                                bounceDPs = 10 + (clicked ? 10 : 0);

                                bouncePhoneDown();
                            }
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {
                            super.onAnimationCancel(animation);

                            gotCancelled = true;
                        }
                    });
        }
    }

    private void bouncePhoneUp() {
        if (bounceDPs == 0 || phoneContainer == null) {
            return;
        }
        phoneContainer.animate().translationYBy(convertDpToPx(getActivity(), bounceDPs) * -1)
                .setDuration(900).setInterpolator(new DecelerateInterpolator())
                .setListener(new AnimatorListenerAdapter() {
                    boolean gotCancelled = false;

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);

                        if (!gotCancelled && bounceDPs != 0) {
                            bounceDPs = 10;

                            bouncePhoneDown();
                        }
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        super.onAnimationCancel(animation);

                        gotCancelled = true;
                    }
                });
    }

    private void bouncePhoneDown() {
        if (bounceDPs == 0 || phoneContainer == null) {
            return;
        }
        phoneContainer.animate().translationYBy(convertDpToPx(getActivity(), bounceDPs))
                .setDuration(900).setInterpolator(new AccelerateInterpolator())
                .setListener(new AnimatorListenerAdapter() {
                    boolean gotCancelled = false;

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);

                        if (!gotCancelled && bounceDPs != 0) {
                            bounceDPs = 10;

                            bouncePhoneUp();
                        }
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        super.onAnimationCancel(animation);

                        gotCancelled = true;
                    }
                });
    }

    /**
     * @param verticalPercentage   0 = just below bottom container rim, 100 = bottom of phone touching rim
     * @param horizontalPercentage 0 = screen center / normal, 100 = phone container's left edge touching Vertical center axis, -100 = phone's right edge touching center axis
     * @param zoomPercentage
     * @param animated
     * @param walkthroughSection
     */
    private void showPhone(int verticalPercentage, final int horizontalPercentage,
                           final int zoomPercentage, boolean animated,
                           final WalkthroughSection walkthroughSection) {
        if (animated) {
            // Don't bounce anymore
            bounceDPs = 0;
        }
        final int distanceFromCenterofPhonetoRight = phoneContainerWidth / 2;
        final int verticalTranslationY = (bottomContainerTop - phoneContainerTop) - ((phoneContainerHeight * verticalPercentage) / 100);

        boolean labelAndPhoneOverlaps = verticalTranslationY - convertDpToPx(getActivity(), 48) <= tutorialButtonBottom;
        boolean versionNumberAndPhoneOverlaps = verticalTranslationY - convertDpToPx(getActivity(), 24) <= lbl2DoVersion.getBottom() || zoomPercentage >= 30;

        if (animated) {
            lbl2DoName.animate().alpha(versionNumberAndPhoneOverlaps ? 0 : 1);
            lbl2DoVersion.animate().alpha(versionNumberAndPhoneOverlaps ? 0 : 1);
            lblWelcomeTitle.animate().alpha(labelAndPhoneOverlaps ? 0 : 1).setDuration(250);
            lblWatchVideo.animate().alpha(labelAndPhoneOverlaps ? 0 : 1).setDuration(250);

            final float finalScaleToUse = 1f + (((float) zoomPercentage) / 100);

            phoneContainer.animate().withLayer()
                    .translationY(verticalTranslationY)
                    .translationX((distanceFromCenterofPhonetoRight * horizontalPercentage) / 100)
                    .scaleX(finalScaleToUse)
                    .scaleY(finalScaleToUse)
                    .setDuration(300)
                    .setInterpolator(new DecelerateInterpolator()).setListener(new AnimatorListenerAdapter() {
                boolean gotCancelled = false;

                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);

                    if (!gotCancelled) {
                        // Prolong effect now
                        if (walkthroughSection != null && phoneContainer != null) {
                            if (walkthroughSection.prolongMovingUp > 0) {
                                phoneContainer.animate().withLayer()
                                        .translationY(verticalTranslationY - convertDpToPx(getActivity(), walkthroughSection.prolongMovingUp))
                                        .setDuration(20 * 1000);
                            } else if (walkthroughSection.prolongMovingDown > 0) {
                                phoneContainer.animate().withLayer()
                                        .translationY(verticalTranslationY + convertDpToPx(getActivity(), walkthroughSection.prolongMovingDown))
                                        .setDuration(20 * 1000);
                            } else if (walkthroughSection.prolongZoomIn > 0) {
                                float extraDps = (((float) convertDpToPx(getActivity(), walkthroughSection.prolongZoomIn))) / 100.0f;

                                phoneContainer.animate().withLayer()
                                        .scaleX(finalScaleToUse + extraDps)
                                        .scaleY(finalScaleToUse + extraDps)
                                        .setDuration(20 * 1000);
                            } else if (walkthroughSection.prolongZoomOut > 0) {
                                float extraDps = (((float) convertDpToPx(getActivity(), walkthroughSection.prolongZoomOut))) / 100.0f;

                                phoneContainer.animate().withLayer()
                                        .scaleX(finalScaleToUse - extraDps)
                                        .scaleY(finalScaleToUse - extraDps)
                                        .setDuration(20 * 1000);
                            }
                        }
                    }
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    super.onAnimationCancel(animation);

                    gotCancelled = true;
                }
            });
        } else {
            lbl2DoName.setAlpha(versionNumberAndPhoneOverlaps ? 0 : 1);
            lbl2DoVersion.setAlpha(versionNumberAndPhoneOverlaps ? 0 : 1);
            lblWelcomeTitle.setAlpha(labelAndPhoneOverlaps ? 0 : 1);
            lblWatchVideo.setAlpha(labelAndPhoneOverlaps ? 0 : 1);
            phoneContainer.setTranslationY(verticalTranslationY);
            phoneContainer
                    .setTranslationX((distanceFromCenterofPhonetoRight * horizontalPercentage) / 100);
            phoneContainer.setScaleX(1f + (((float) zoomPercentage) / 100));
            phoneContainer.setScaleY(1f + (((float) zoomPercentage) / 100));
        }
    }
    //endregion

    //region Button Actions
    public void doneClicked() {
        bounceDPs = 0;
        phoneContainer.animate().cancel();
        getActivity().finish();
    }
    //endregion

    //region Pager Listener
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        Log.v("Walkthrough", "Page Selected: " + position);
        WalkthroughSection section = walkthroughSectionList.get(position);

        loadImageResource(section.sectionShotName);

        if (position == 0) {
            revealPhoneFirstTime(300, false);
        } else {
            showPhone(section.sectionDeviceVerticalPercToShow, section.sectionDeviceHorizontalPercToShow,
                    section.sectionDeviceZoomPercToShow, true, section);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
    //endregion

    //region Image Loading
    private void loadImageResource(@DrawableRes final int imageId) {
        new Thread(new Runnable() {
            public void run() {
                if (imgPhoneShot != null) {
                    final Resources resources = getActivity().getResources();
                    final Bitmap imageBitmap = decodeSampledBitmapFromResource(resources, imageId,
                            (int) (phoneContainerWidth * 0.5f), (int) (phoneContainerHeight * .5f));

                    // Set the new image to transition to
                    TransitionDrawable transitionDrawable = null;

                    final Drawable[] layers = new Drawable[2];

                    Drawable oldDrawable = imgPhoneShot.getDrawable();
                    BitmapDrawable oldBitmapDrawable = null;
                    if (oldDrawable instanceof TransitionDrawable) {
                        TransitionDrawable oldTransitionDrawable = (TransitionDrawable) oldDrawable;
                        oldBitmapDrawable = (BitmapDrawable) (oldTransitionDrawable).getDrawable(1);
                    } else if (oldDrawable instanceof BitmapDrawable) {
                        oldBitmapDrawable = (BitmapDrawable) oldDrawable;
                    }

                    if (oldBitmapDrawable != null && imageBitmap != null) {
                        layers[0] = oldBitmapDrawable;
                        layers[1] = new BitmapDrawable(resources, imageBitmap);

                        transitionDrawable = new TransitionDrawable(layers);
                        transitionDrawable.setCrossFadeEnabled(true);
                    }

                    final TransitionDrawable finalTransitionDrawable = transitionDrawable;
                    uiHandler.post(new Runnable() {
                        public void run() {
                            if (imgPhoneShot != null) {
                                if (finalTransitionDrawable != null) {
                                    imgPhoneShot.setImageDrawable(finalTransitionDrawable);
                                    finalTransitionDrawable.startTransition(150);
                                } else if (imageBitmap != null) {
                                    imgPhoneShot.setImageBitmap(imageBitmap);
                                }
                            }
                        }
                    });
                }
            }

        }).start();
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            if (halfHeight > 0 && halfWidth > 0) {
                try {
                    while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                        inSampleSize *= 2;
                    }
                } catch (ArithmeticException ex) {

                }
            }
        }

        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        try {
            return BitmapFactory.decodeResource(res, resId, options);
        } catch (Exception ex) {
            return null;
        }
    }
    //endregion

    //region Pager Adapter
    private class WalkthroughInternalPagerAdapter extends FragmentStatePagerAdapter {
        public WalkthroughInternalPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            WalkthroughContentFragment contentFragment = new WalkthroughContentFragment();
            contentFragment.setWalkthroughSection(walkthroughSectionList.get(position));
            return contentFragment;
        }

        @Override
        public int getCount() {
            return walkthroughSectionList.size();
        }
    }
    //endregion

    //region Helper Methods
    public static float convertDpToPx(Context context, float dp) {
        if (context == null) {
            return 0;
        }
        Resources res = context.getResources();

        return dp * (res.getDisplayMetrics().densityDpi / 160f);
    }
    //endregion
}
