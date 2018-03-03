package main.java.com.dlohaiti.dlokiosk;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.Toast;
import main.java.com.dlohaiti.dlokiosk.client.*;
import main.java.com.dlohaiti.dlokiosk.db.*;
import main.java.com.dlohaiti.dlokiosk.domain.*;
import com.google.inject.Inject;
import roboguice.inject.InjectResource;
import roboguice.util.RoboAsyncTask;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.dlohaiti.dlokiosk.R;

public class PullConfigurationTask extends RoboAsyncTask<Boolean> {
    private static final String TAG = PullConfigurationTask.class.getSimpleName();
    private ProgressDialog dialog;
    @Inject private ConfigurationClient client;
    @Inject private ProductRepository productRepository;
    @Inject private PromotionRepository promotionRepository;
    @Inject private SamplingSiteParametersRepository samplingSiteParametersRepository;
    @Inject private DeliveryAgentRepository deliveryAgentRepository;
    @Inject private ConfigurationRepository configurationRepository;
    @Inject private KioskDate kioskDate;
    @Inject private Base64ImageConverter imageConverter;
    @InjectResource(R.string.fetch_configuration_failed) private String fetchConfigurationFailedMessage;
    @InjectResource(R.string.fetch_configuration_succeeded) private String fetchConfigurationSucceededMessage;
    @InjectResource(R.string.update_configuration_failed) private String updateConfigurationFailedMessage;
    @InjectResource(R.string.loading_configuration) private String loadingConfigurationMessage;
    private Context context;

    public PullConfigurationTask(Context context) {
        super(context);
        this.context = context;
        this.dialog = new ProgressDialog(context);
    }

    @Override protected void onPreExecute() throws Exception {
        dialog.setMessage(loadingConfigurationMessage);
        dialog.show();
    }

    @Override public Boolean call() throws Exception {
        Configuration c = client.fetch();
        List<Product> products = new ArrayList<Product>();
        for (ProductJson p : c.getProducts()) {
            Money price = new Money(p.getPrice().getAmount());
            Bitmap imageResource = imageConverter.fromBase64EncodedString(p.getBase64EncodedImage());
            products.add(new Product(null, p.getSku(), imageResource, p.isRequiresQuantity(), 1, p.getMinimumQuantity(), p.getMaximumQuantity(), price, p.getDescription(), p.getGallons()));
        }
        List<Promotion> promotions = new ArrayList<Promotion>();
        for (PromotionJson p : c.getPromotions()) {
            PromotionApplicationType appliesTo = PromotionApplicationType.valueOf(p.getAppliesTo());
            Date start = kioskDate.getFormat().parse(p.getStartDate());
            Date end = kioskDate.getFormat().parse(p.getEndDate());
            Bitmap imageResource = imageConverter.fromBase64EncodedString(p.getBase64EncodedImage());
            promotions.add(new Promotion(null, p.getSku(), appliesTo, p.getProductSku(), start, end, p.getAmount().toString(), PromotionType.valueOf(p.getType()), imageResource));
        }
        List<ParameterSamplingSites> samplingSiteParameters = new ArrayList<ParameterSamplingSites>();
        for (ParameterJson p : c.getParameters()) {
            Parameter parameter = new Parameter(p.getName(), p.getUnit(), p.getMinimum(), p.getMaximum(), p.isOkNotOk(), p.getPriority());
            List<SamplingSite> samplingSites = new ArrayList<SamplingSite>();
            for (SamplingSiteJson site : p.getSamplingSites()) {
                samplingSites.add(new SamplingSite(site.getName()));
            }
            samplingSiteParameters.add(new ParameterSamplingSites(parameter, samplingSites));
        }
        List<DeliveryAgent> agents = new ArrayList<DeliveryAgent>();
        for (DeliveryAgentJson agent : c.getDelivery().getAgents()) {
            agents.add(new DeliveryAgent(agent.getName()));
        }

        DeliveryConfigurationJson configuration = c.getDelivery().getConfiguration();

        //FIXME: what happens when one of these fails?
        return configurationRepository.save(ConfigurationKey.DELIVERY_TRACKING_MIN, configuration.getMinimum()) &&
            configurationRepository.save(ConfigurationKey.DELIVERY_TRACKING_MAX, configuration.getMaximum()) &&
            configurationRepository.save(ConfigurationKey.DELIVERY_TRACKING_DEFAULT, configuration.getDefault()) &&
            productRepository.replaceAll(products) &&
                promotionRepository.replaceAll(promotions) &&
                samplingSiteParametersRepository.replaceAll(samplingSiteParameters) &&
                deliveryAgentRepository.replaceAll(agents);
    }

    @Override protected void onSuccess(Boolean s) throws Exception {
        if(s.equals(Boolean.TRUE)) {
            Toast.makeText(context, fetchConfigurationSucceededMessage, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(context, updateConfigurationFailedMessage, Toast.LENGTH_LONG).show();
        }
    }

    @Override protected void onException(Exception e) throws RuntimeException {
        Log.e(TAG, "Error fetching configuration from server", e);
        Toast.makeText(context, fetchConfigurationFailedMessage, Toast.LENGTH_LONG).show();
    }

    @Override protected void onFinally() throws RuntimeException {
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}
